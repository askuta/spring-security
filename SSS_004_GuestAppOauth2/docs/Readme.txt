Spring Security
===============

Frank P Moley III
Spring: Spring Security
https://learn.epam.com/detailsPage?id=a0b7bf08-35b6-49cc-be9c-6afec2a58f8c


Passwords:
- fpmoles: password
- jdoe: foobar

Oauth 2 authentication
----------------------



     -----=====     In the SERVICES application!     =====-----



Add the @EnableResourceServer annotation to the application:

  @SpringBootApplication
  @EnableResourceServer
  public class GuestAppOauth2Application { ... }


Add Maven dependency:

  <!-- For Oauth 2. -->
  <dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>2.3.0.RELEASE</version>
  </dependency>


Add configuration for Oauth2:

  @EnableAuthorizationServer
  @Configuration
  public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
      security.passwordEncoder(NoOpPasswordEncoder.getInstance())
          .checkTokenAccess("permitAll()")
          .tokenKeyAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.inMemory()
          .withClient("guest_app")
          .scopes("READ_ALL_GUESTS", "WRITE_GUEST", "UPDATE_GUEST")
          .secret("secret")
          .autoApprove(true)
          .authorities("ROLE_GUESTS_AUTHORIZED_CLIENT")
          .authorizedGrantTypes("client_credentials")
          .and()
          .withClient("api_audit")
          .scopes("READ_ALL_GUESTS")
          .secret("secret")
          .autoApprove(true)
          .authorities("ROLE_GUESTS_AUTHORIZED_CLIENT")
          .authorizedGrantTypes("client_credentials");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
      endpoints.tokenStore(new InMemoryTokenStore());
    }
  }


Generate access token:

  Run the SSS_004_GuestServicesOauth2 application.

  Encode password(?) for "guest_app:secret":
  - http://www.tuxgraphics.org/toolbox/base64-javascript.html

  username:password --> guest_app:secret
  base64 encoded result --> Z3Vlc3RfYXBwOnNlY3JldA==

  Generate the token:
  > http --form POST localhost:8100/oauth/token Authorization:"Basic Z3Vlc3RfYXBwOnNlY3JldA==" grant_type=client_credentials

    Or use Postman:
      POST
      localhost:8100/oauth/token
      Body - select "form-data"
        - Authorization:"Basic Z3Vlc3RfYXBwOnNlY3JldA=="
        - grant_type=client_credentials

    Result:
      {
        "access_token": "b3d1b1e4-7305-4ca3-af9e-beed787bcc69",
        "token_type": "bearer",
        "expires_in": 43199,
        "scope": "READ_ALL_GUESTS WRITE_GUEST UPDATE_GUEST"
      }

  Check the token:
  > http --form localhost:8100/oauth/check_token Authorization:"Bearer [access-token]" token=[access-token]


  Encode password(?) for "api_audit:secret":
  - http://www.tuxgraphics.org/toolbox/base64-javascript.html

  username:password --> api_audit:secret
  base64 encoded result --> YXBpX2F1ZGl0OnNlY3JldA==

  Generate the token:
  > http --form POST localhost:8100/oauth/token Authorization:"Basic YXBpX2F1ZGl0OnNlY3JldA==" grant_type=client_credentials

    Result:
      {
        "access_token": "b3d1b1e4-7305-4ca3-af9e-beed787bcc69",
        "token_type": "bearer",
        "expires_in": 42891,
        "scope": "READ_ALL_GUESTS WRITE_GUEST UPDATE_GUEST"
      }



     -----=====     In the GUEST APP application!     =====-----



Add Maven dependency to the GuestApp application too:

  <!-- For Oauth 2. -->
  <dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>2.3.0.RELEASE</version>
  </dependency>


Enable Oauth and create a RestTemplate (OAuth2RestTemplate) bean:

  @SpringBootApplication
  @EnableOAuth2Client
  public class GuestAppOauth2Application {

    private static final String AUTH_TOKEN_URL = "/oauth/token";

    @Value("${landon.guest.service.url}")
    private String serviceUrl;

    public static void main(String[] args) {
      SpringApplication.run(GuestAppOauth2Application.class, args);
    }

    @Bean
    public OAuth2RestTemplate restTemplate() {
      ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
      resource.setAccessTokenUri(serviceUrl + AUTH_TOKEN_URL);
      resource.setClientId("guest_app");
      resource.setClientSecret("secret");
      resource.setGrantType("client_credentials");
      resource.setScope(Arrays.asList("READ_ALL_GUESTS", "WRITE_GUEST", "UPDATE_GUEST"));
      resource.setAuthenticationScheme(AuthenticationScheme.form);
      AccessTokenRequest request = new DefaultAccessTokenRequest();

      return new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(request));
    }
  }


Autowire the RestTemplate bean to the GuestService:

  @Service
  public class GuestService {

    :
    private final RestTemplate restTemplate;

    @Autowired
    public GuestService(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
    }
    :
  }
