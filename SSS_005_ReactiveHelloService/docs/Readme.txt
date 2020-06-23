Spring Security
===============

Frank P Moley III
Spring: Spring Security
https://learn.epam.com/detailsPage?id=a0b7bf08-35b6-49cc-be9c-6afec2a58f8c


Passwords:
- fpmoles: password
- jdoe: foobar

WebFlux Security
----------------

Maven dependencies:

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>


Enable WebFlux Security, add userDetailsService and securityFilterChain:

  @SpringBootApplication
  @EnableWebFluxSecurity
  public class ReactiveHelloServiceApplication {

    public static void main(String[] args) {
      SpringApplication.run(ReactiveHelloServiceApplication.class, args);
    }

    @Bean
    @SuppressWarnings("deprecation")
    public MapReactiveUserDetailsService userDetailsService() {
      List<UserDetails> userDetails = new ArrayList<>();
      userDetails.add(User.withDefaultPasswordEncoder().username("fpmoles").password("password").roles("USER", "ADMIN").build());
      userDetails.add(User.withDefaultPasswordEncoder().username("jdoe").password("foobar").roles("USER").build());

      return new MapReactiveUserDetailsService(userDetails);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
      http.authorizeExchange()
          .pathMatchers("/hello").permitAll()
          .anyExchange().hasRole("ADMIN")
          .and()
          .httpBasic();

      return http.build();
    }
  }
