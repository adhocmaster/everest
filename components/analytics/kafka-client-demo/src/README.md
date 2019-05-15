# Demo Kafka Clients: Producer and Consumer for testing Kafka deployment/installation

- [Demo Kafka Clients: Producer and Consumer for testing Kafka deployment/installation](#demo-kafka-clients-producer-and-consumer-for-testing-kafka-deploymentinstallation)
  - [Prerequisites](#prerequisites)
  - [Getting Started](#getting-started)
  - [Contributing](#contributing)
  - [License](#license)
  - [Acknowledgments](#acknowledgments)
  - [FAQs](#faqs)

## Prerequisites
The prerequisites are:
- **Python and pip** - Python 2.7.x or 3.5.x (and/or virtualenv or conda)
```
Install Python
Install pip
```
- **Python Package for Kafka client** - 
```
% pip install kafka-python
```
- **Running Kafka cluster** - It is not important to have it installed and running -

## Getting Started

- Find out the bootstrapper address of kafka (e.g. broker address)
```
% python kafka-demo-consumer.py -b BROKER_ADDRESS
```
In different terminal or host
```
% python kafka-demo-producer.py -b BROKER_ADDRESS
```

## Contributing

TBD

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

TBD

## FAQs

TBD
