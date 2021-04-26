# Floricultura microservices parent
Microserviços de uma floricultura feitos utilizando Spring Cloud e suas tecnologias.

## Microservices
- **LOJA:** realiza a compra se comunicando com os demais serviços;
- **FORNECDOR:** CRUD produtos, CRUD pedidos, CRUD fornecedores;

## Spring Eureka Server

### Service Registry

- Para habilitar o serviço com Eureka como registry, é necessário adicionar a anotação **@EnableEurekaServer** e importar a dependência do **eureka-server**.

- Para **registrar** um client no Eureka, basta adicionar as seguintes propriedades em cada client, além de importar a dependência **eureka-client**:
```
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Load Balancer

- **Ribbon** é uma lib do SpringCloud de **Client Side Load Balancing** onde o cliente vai decidir qual instância do serviço vai receber a requisição. RestTemplate e FeignClient possuem suporte.

- **RestTemplate**: podemos resolver o nome dos host dos clientes registrados no Eureka adicionando a anotação **@LoadBalanced** no Bean de RestTemplate de cada client. Com disso, o **load balancer** também será habilitado nas requisições.

- **FeignClient**: É uma lib que permite consumir as aplicações registradas no Eureka com loadbalancer. Deve-se adicionar **@EnableFeignClients** na classe main e criar interfaces com os métodos e anotações que mapearão as requisições.