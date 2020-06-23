Spring Security
===============

Frank P Moley III
Spring: Spring Security
https://learn.epam.com/detailsPage?id=a0b7bf08-35b6-49cc-be9c-6afec2a58f8c


Passwords:
- fpmoles: password
- jdoe: foobar

Form-based authentication
-------------------------

Add Maven dependency:

  <dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity4</artifactId>
    <version>3.0.4.RELEASE</version>
  </dependency>


Edit Thymeleaf templates


Add controllers:

  @GetMapping(value = "/login")
  public String getLoginPage(Model model) {
    return "login";
  }

  @GetMapping(value = "/logout-success")
  public String getLogoutPage(Model model) {
    return "logout";
  }


Configure form based authentication:

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
        .formLogin()
        .loginPage("/login").permitAll()
        .and()
        .logout().invalidateHttpSession(true)
        .clearAuthentication(true)
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/logout-success").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authenticationProvider());
    }
  }
