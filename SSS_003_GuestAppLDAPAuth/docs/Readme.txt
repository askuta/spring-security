Spring Security
===============

Frank P Moley III
Spring: Spring Security
https://learn.epam.com/detailsPage?id=a0b7bf08-35b6-49cc-be9c-6afec2a58f8c


Passwords:
- fpmoles: password
- jdoe: foobar

LDAP authentication
-------------------


Remove all JPA stuff.


Add Maven dependency:

  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-ldap</artifactId>
  </dependency>
  <dependency>
    <groupId>com.unboundid</groupId>
    <artifactId>unboundid-ldapsdk</artifactId>
  </dependency>


Add LDAP data:

  landon.ldif


Update configs:

  public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper(){
      SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
      authorityMapper.setConvertToUpperCase(true);
      authorityMapper.setDefaultAuthority("USER");
      return authorityMapper;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .ldapAuthentication()
          .userDnPatterns("uid={0},ou=people")
          .groupSearchBase("ou=groups")
          .authoritiesMapper(authoritiesMapper())
          .contextSource()
          .url("ldap://localhost:8389/dc=frankmoley,dc=com")
          .and()
          .passwordCompare()
          .passwordEncoder(new LdapShaPasswordEncoder())
          .passwordAttribute("userPassword");
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
  }
