package club.tulane.kafkademo.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Slf4j
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private static final String TOPIC = "test-topic";

//	@Bean
//	public ProducerFactory<String, String> producerFactory() {
//		return new DefaultKafkaProducerFactory<>(producerProps());
//	}
//
//	@Bean
//	public KafkaTemplate<String, String> kafkaTemplate() {
//		return new KafkaTemplate<>(producerFactory());
//	}
//
//	private Map<String, Object> producerProps() {
//		Map<String, Object> props = new HashMap<>(10);
//		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9001,localhost:9002,localhost:9003");
//		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
//		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		return props;
//	}

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;


	@GetMapping("/send/{input}")
	public void sendFoo(@PathVariable String input) {
		this.kafkaTemplate.send(TOPIC, input);
	}
	@KafkaListener(id = "webGroup", topics = TOPIC)
	public void listen(String input) {
		log.info("input value: {}" , input);
	}
}
