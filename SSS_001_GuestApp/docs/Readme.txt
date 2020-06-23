Spring Security
===============

Frank P Moley III
Spring: Spring Security
https://learn.epam.com/detailsPage?id=a0b7bf08-35b6-49cc-be9c-6afec2a58f8c

Basic authentication
--------------------

This is a first match wins so you may need to play with the order.

  @Configuration
  @EnableWebSecurity
  public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()
          .authorizeRequests()
          .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
    }
  }

  - permit access to: "/", "/index", "/css/*", "/js/*"
  - authenticate everything else
  - with basic authentication


In-memory authenticaion
-----------------------

Never use it in production!

  @Configuration
  @EnableWebSecurity
  public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()
          .authorizeRequests()
          .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
    }

    @SuppressWarnings("deprecation")
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
      List<UserDetails> users = new ArrayList<>();
      users.add(User.withDefaultPasswordEncoder().username("fpmoles").password("password").roles("USER", "ADMIN").build());
      users.add(User.withDefaultPasswordEncoder().username("jdoe").password("foobar").roles("USER").build());

      return new InMemoryUserDetailsManager(users);
    }
  }


JDBC authenticaion
------------------

  @Service
  public class LandonUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = userRepository.findByUsername(username);
      if (null == user) {
        throw new UsernameNotFoundException("Cannot find username: " + username);
      }

      return new LandonUserPrincipal(user);
    }
  }

  @SuppressWarnings("deprecation")
  @Configuration
  @EnableWebSecurity
  public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private LandonUserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(userDetailsService);
      provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

      return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()
          .authorizeRequests()
          .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authenticationProvider());
    }
  }


Using bcrypt for hashing
------------------------

  @Configuration
  @EnableWebSecurity
  public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private LandonUserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(userDetailsService);
      provider.setPasswordEncoder(new BCryptPasswordEncoder(11));

      return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()
          .authorizeRequests()
          .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authenticationProvider());
    }
  }
