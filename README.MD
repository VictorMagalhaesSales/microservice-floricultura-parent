# Floricultura microservices parent
> Microserviços de uma floricultura feitos utilizando **Spring Cloud**, implementando um Autorization Server, API Gateway, Service Registry, Load Balancer, Tracing Distribuído e um Circuit Breaker.
<br>

## Microservices
- **AUTH-SERVER**: atua como um servidor de autorização OAuth2.
- **ZULL-GATEWAY**: atua como API Gateway.
- **EUREKA-SERVER**: atua como serviço de registro das nossas aplicações;
- **LOJA:** realiza a compra se comunicando com os demais serviços. Banco de dados único;
- **FORNECDOR:** CRUD produtos, CRUD pedidos, CRUD fornecedores. Banco de dados único;
- **TRANSPORTADOR:** realiza a entrega. Banco de dados único;
<br>

## Authorization Server - Oauth2

- Habilitando o projeto como servidor de autorização em **[AuthorizationServerConfigurer](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/auth-server/src/main/java/br/com/microservice/sso/configuration/AuthorizationServerConfigurer.java)** e criando o client *loja* e secret *secret* em memória.
- Habilitando o projeto como servidor de recursos em **[AuthorizationSsoApplication](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/auth-server/src/main/java/br/com/microservice/sso/AuthorizationSsoApplication.java)** para então criar um @RestController com endpoint `/user` retornando os dados do usuário autenticado.
- Configurando o spring security em **[SpringSecurityConfigurer](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/auth-server/src/main/java/br/com/microservice/sso/configuration/SpringSecurityConfigurer.java)** e criando o usuário *victor* com senha *password* em memória.
- Habilitando os serviços *fornecedor* e *loja* como resouce server através de **[@EnableResourceServer](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/java/br/com/alura/microservice/fornecedor/FornecedorApplication.java)**, extensão de **[ResourceServerConfigurerAdapter](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/java/br/com/alura/microservice/fornecedor/ResourceServerConfigurer.java)** e referência de AuthServer em **[application.yml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/application.yml)**(`security.oauth2.resource.user-info-uri`).
<br>

## API Gateway - Zuul
- Para habilitar o **[projeto](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/tree/master/zuul-gateway)** como **API Gateway**, é necessário importar o starter **netflix-zuul**, adicionar a anotação `@netflix` e as **[propriedades](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/zuul-gateway/src/main/resources/application.yml)** do eureka server para recuperar todas as aplicações registradas e fazer o proxy.
- Compartilhamos os headers de autorização que são recebidos nas requisições em **[application.yml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/zuul-gateway/src/main/resources/application.yml)**.
<br>

## Service Registry - Spring Eureka

- Para habilitar o [projeto](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/tree/master/eureka-server) como **Service Registry**, é necessário importar o starter **netflix-eureka-server** e adicionar a anotação `@EnableEurekaServer`.

- **Eureka Client** nos permite registrar um client(*loja* e *fornecedor*) no Eureka adicionando as propriedades referentes ao seu registro em seu **[application.yml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/application.yml)**.
<br>

## Load Balancer - Ribbon

- **Ribbon:** lib do SpringCloud de **Client Side Load Balancing** onde o cliente decidirá qual instância do serviço receberá a requisição. *RestTemplate* e *FeignClient* possuem suporte.

- **RestTemplate:** podemos resolver o nome dos host dos clientes registrados no Eureka com a anotação `@LoadBalancer` o que habilita, também, o **C.S. Load Balancing** nas requisições.

- **FeignClient:** permite consumir as aplicações registradas no Eureka com Load Balancer. Deve-se adicionar `@EnableFeignClients` na classe main e criar interfaces([example](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/loja/src/main/java/br/com/alura/microservice/loja/client/FornecedorClient.java)) com os métodos e anotações que mapearão as requisições.
- Para refletir **dados das requisições** recebidas, criamos um **[RequestInterceptor](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/loja/src/main/java/br/com/alura/microservice/loja/LojaApplication.java)** e copiamos o **token** para o header da requisição realizada pelo Feign.
<br>

## Tracing distribuído - Spring Sleuth

- **Spring Sleuth:** criará um **Trace ID** que identificará a transação na comunicação entre os serviços. Para mostrá-los nos logs, basta modificar os patterns de **[logback.xml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/logback.xml)** onde for desejado. Exemplo:
```
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %clr(${LOG_LEVEL_PATTERN}) %clr(${PID:-}){magenta} [%thread] %clr(%logger{39}){cyan} - %msg%n</pattern>
```

- **Papertrail:** permite **centralizar logs** informando suas propriedades de conexão em **[logback.xml](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/fornecedor/src/main/resources/logback.xml)**. 
<br>

## Circuit Breaker - Hystrix

- **Hystrixs:** utilizaremos `@EnableCircuitBreaker` para habilitá-lo na app e [`@HystrixCommand`](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/loja/src/main/java/br/com/alura/microservice/loja/service/CompraService.java) nos métodos para que a lib o gerencie e aplique suas funcionalidades. O Hystrix passará a gerenciar as threads.
    - Um **fallback** pode ser definido para um método que será chamado quando o Circuit Breaker ocorrer: `@HystrixCommand(fallbackMethod="realizaCompraFallback")`.
    - Se houver **muitas falhas**, o Hystrix possui inteligencia de chamar o fallback por padrão durante um tempo.
    - **Bulkhead:** para evitar que, ao haver uma sobrecarga de chamadas para uma operação, outra operação fique sem threads disponíveis, criaremos um **pool de thread** separado para cada metodo: `@HystrixCommand(threadPoolKey="key")`.
    - Configuramos **[`shareSecurityContext: true`](https://github.com/VictorMagalhaesSales/microservice-floricultura-parent/blob/master/loja/src/main/resources/application.yml)** para compartilhar o **contexto de seguraça** entre as threads gerenciadas pelo Hystrix.
