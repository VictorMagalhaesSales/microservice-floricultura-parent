
## Spring Eureka Server
- Necessário adicionar a anotação **@EnableEurekaServer** para habilitar o EurekaServer como registry server.
- Para **se registrar** no Eureka, basta adicionar as propriedades:
```
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```
- Anotação **@LoadBalanced**, no Bean de RestTemplate dos projetos, permite que o mesmo resolva os nomes das aplicações registradas no eureka-server.