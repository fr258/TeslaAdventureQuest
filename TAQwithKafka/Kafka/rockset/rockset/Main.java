import static spark.Spark.*;
import com.google.gson.Gson;
import com.smartcar.sdk.*;
import com.smartcar.sdk.data.*;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.admin.*;

import java.io.*;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class Main {

  public static void createTopic(final String topic,
                          final Properties cloudConfig) {
      final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
      try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
          adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
      } catch (final InterruptedException | ExecutionException e) {
          // Ignore if TopicExistsException, which may be valid if topic exists
          if (!(e.getCause() instanceof TopicExistsException)) {
              throw new RuntimeException(e);
          }
      }
  } 
public static void main(String[] args) throws IOException {

 final Properties props = loadConfig("./src/main/java/java.config");

    //Topic
    final String topic = "questTopic";
    createTopic(topic, props);

    // Add additional properties.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
	props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String,String> producer = new KafkaProducer<String, String>(props);

    // Produce sample data
    final Long numMessages = 10L;
    for (Long i = 0L; i < numMessages; i++) {
      String key = "testing";
    

      System.out.printf("Producing record: %s\t%n", key);
      producer.send(new ProducerRecord<String,String>(topic, key, "{\"javaTesting6\": 4, \"javaTesting5\": \"javaTest5\"}"), new Callback() {
          @Override
          public void onCompletion(RecordMetadata m, Exception e) {
            if (e != null) {
              e.printStackTrace();
            } 
          }
      });
    }

    producer.flush();
    producer.close();

 /* // global variable to save our accessToken
  private static String access;
  private static Gson gson = new Gson();
private static int value = 0;
  public static void main(String[] args) {
 // Check arguments length value
	
    port(8000);

    String clientId = System.getenv("CLIENT_ID");
    String clientSecret = System.getenv("CLIENT_SECRET");
    String redirectUri = "http://localhost:8000/exchange";
    String[] scope = {"required:read_vehicle_info", "read_location", "read_battery"};
    boolean testMode = true;

    AuthClient client = new AuthClient(
      "265a0928-3ae3-44e5-beb7-1fbd6e636738",
      "6729c326-bf1b-431a-821d-48a10ef92634",
      redirectUri,
      scope,
      true
    );

    get("/login", (req, res) -> {
      String link = client.getAuthUrl();
      res.redirect(link);
      return null;
    });

    get("/exchange", (req, res) -> {
      String code = req.queryMap("code").value();

      Auth auth = client.exchangeCode(code);

      // in a production app you'll want to store this in some kind of persistent storage
      access = auth.getAccessToken();

      return "";
    });

    get("/vehicleLocation", (req, res) -> {
      SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
      // the list of vehicle ids
      String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();

      // instantiate the first vehicle in the vehicle id list
      Vehicle vehicle = new Vehicle(vehicleIds[0], access);

      VehicleInfo info = vehicle.info(); 
      //VehicleLocation location =  vehicle.location()
      

      System.out.println(gson.toJson(info));

      SmartcarResponse<VehicleLocation> response = vehicle.location();
      System.out.println(response.getData());

      // {
      //   "id": "36ab27d0-fd9d-4455-823a-ce30af709ffc",
      //   "make": "TESLA",
      //   "model": "Model S",
      //   "year": 2014
      // }

      res.type("application/json");

producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(value++), response.getData().toString()));	
      return response.getData();//gson.toJson(info);
    });

  get("/vehicleRange", (req, res) -> {
      SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
      String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();
      Vehicle vehicle = new Vehicle(vehicleIds[0], access);

      SmartcarResponse response = vehicle.battery();
      System.out.println(response);

      res.type("application/json");
producer.send(new ProducerRecord<String, String>(topicName,Integer.toString(value++), response.getData().toString()));	
      return response;

    });
*/	
  }

public static Properties loadConfig(final String configFile) throws IOException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }
}
