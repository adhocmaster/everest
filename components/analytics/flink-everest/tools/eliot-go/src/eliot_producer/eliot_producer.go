package main

import (
	// "context"
	// "encoding/json"
	"flag"
	"fmt"
	// "net/http"
	"os"
	"os/signal"
	"strings"
	"syscall"

	"strconv"

	// "github.com/gin-gonic/gin"
	"github.com/rs/zerolog/log"

	//eliot kafka specific libraries
	"kafka_producer"
)

const (
	defaultKafkaBrokerURL string = "bootstrap.default.svc.cluster.local:9092,master:32400,localhost:9092"
	defaultListenAddrAPI  string = "0.0.0.0:9000"
	defaultKafkaVerbose   bool   = false
	defaultKafkaInTopic   string = "in-topic"
	defaultDuration       string = "5m"
	defaultGatewayNumber  int    = 1
	defaultGatewayPrefix  string = "ST"
	defaultSensorNumber   int    = 2
	defaultSensorPrefix   string = "SID"
	defaultKafkaClientID  string = "kafka-eliot-client"
)

var (
	logger        = log.With().Str("pkg", "main").Logger()
	listenAddrApi *string

	// kafka
	kafkaBrokerUrl *string
	kafkaVerbose   *bool
	kafkaClientId  *string
	kafkaInTopic   *string
	duration       *string
	gatewayNumber  *int
	gatewayPrefix  *string
	sensorNumber   *int
	sensorPrefix   *string
	err            error
	
)

func main() {

	listenAddrApi = flag.String("a", defaultListenAddrAPI, "Listen address for api")
	kafkaBrokerUrl = flag.String("k", defaultKafkaBrokerURL, "Kafka brokers in comma separated value")
	kafkaVerbose = flag.Bool("v", defaultKafkaVerbose, "Kafka verbose logging")
	kafkaInTopic = flag.String("i", defaultKafkaInTopic, "Kafka topic to push in")
	duration = flag.String("d", defaultDuration, "Duration")
	gatewayNumber = flag.Int("g", defaultGatewayNumber, "Number of Gateways")
	gatewayPrefix = flag.String("gp", defaultGatewayPrefix, "Prefix to use for Gateway ID")
	sensorNumber = flag.Int("s", defaultSensorNumber, "Number of sensors")
	sensorPrefix = flag.String("sp", defaultSensorPrefix, "Prefix to use for sensor ID")
	kafkaClientId = flag.String("id", defaultKafkaClientID, "Kafka client id to connect")

	flag.Parse()

	fmt.Println("listenAddrApi:", *listenAddrApi)
	fmt.Println("kafkaBrokerUrl:", *kafkaBrokerUrl)
	fmt.Println("kafkaVerbose:", *kafkaVerbose)
	fmt.Println("kafkaInTopic:", *kafkaInTopic)
	fmt.Println("duration:", *duration)
	fmt.Println("gatewayNumber:", *gatewayNumber)
	fmt.Println("gatewayPrefix:", *gatewayPrefix)
	fmt.Println("sensorNumber:", *sensorNumber)
	fmt.Println("sensorPrefix:", *sensorPrefix)
	fmt.Println("kafkaClientId:", *kafkaClientId)
	fmt.Println("tail:", flag.Args())

	bootstrapper := os.Getenv("ELIOT_SIM_KAFKA_BOOTSTRAPPER")
	inTopic := os.Getenv("ELIOT_SIM_KAFKA_IN_TOPIC")
	durationEnv := os.Getenv("ELIOT_SIM_DURATION")
	gwNumber := os.Getenv("ELIOT_SIM_GW_NUMBER")
	gwPref := os.Getenv("ELIOT_SIM_GW_PREFIX")
	sensorNumberEnv := os.Getenv("ELIOT_SIM_SENSOR_NUMBER")
	sensorPref := os.Getenv("ELIOT_SIM_SENSOR_PREFIX")
	verbose := os.Getenv("ELIOT_SIM_VERBOSE")

	if *kafkaBrokerUrl == defaultKafkaBrokerURL && bootstrapper != "" {
		*kafkaBrokerUrl = bootstrapper
	}
	if *kafkaInTopic == defaultKafkaInTopic && inTopic != "" {
		*kafkaInTopic = inTopic
	}
	if *duration == defaultKafkaInTopic && durationEnv != "" {
		*duration = durationEnv
	}

	if(*gatewayNumber == defaultGatewayNumber && gwNumber != "") {
	    *gatewayNumber, err = strconv.Atoi(gwNumber)
	    if err != nil {
			logger.Error().Str("error", err.Error()).Msg("unable to parse gw number")
	    }
	}
	if(*gatewayPrefix == defaultGatewayPrefix && gwPref != "") {
	    *gatewayPrefix = gwPref
	}
	if(*sensorNumber == defaultSensorNumber && sensorNumberEnv != "") {
		*sensorNumber, err = strconv.Atoi(sensorNumberEnv)
		if err != nil {
			logger.Error().Str("error", err.Error()).Msg("unable to parse sensor number")
	    }
	}
	if *sensorPrefix == defaultSensorPrefix && sensorPref != "" {
		*sensorPrefix = sensorPref
	}
	if(*kafkaVerbose == defaultKafkaVerbose && verbose != "") {
		*kafkaVerbose, err = strconv.ParseBool(verbose)
		if err != nil {
			logger.Error().Str("error", err.Error()).Msg("unable to parse verbose")
	    }
		
	}

	fmt.Println("ELIOT_SIM_KAFKA_BOOTSTRAPPER:", *kafkaBrokerUrl)
	fmt.Println("ELIOT_SIM_KAFKA_IN_TOPIC:", *kafkaInTopic)
	fmt.Println("ELIOT_SIM_DURATION:", *duration)
	fmt.Println("ELIOT_SIM_GW_NUMBER:", *gatewayNumber)
	fmt.Println("ELIOT_SIM_GW_PREFIX:", *gatewayPrefix)
	fmt.Println("ELIOT_SIM_SENSOR_NUMBER:", *sensorNumber)
	fmt.Println("ELIOT_SIM_SENSOR_PREFIX:", *sensorPrefix)
	fmt.Println("ELIOT_SIM_VERBOSE:", *kafkaVerbose)

	os.Exit(0)

	// connect to kafka
	kafkaProducer, err := kafka_producer.Configure(strings.Split(*kafkaBrokerUrl, ","), *kafkaClientId, *kafkaInTopic)
	if err != nil {
		logger.Error().Str("error", err.Error()).Msg("unable to configure kafka")
		return
	}
	defer kafkaProducer.Close()
	var errChan = make(chan error, 1)

	// go func() {
	// 	log.Info().Msgf("starting server at %s", listenAddrApi)
	// 	errChan <- server(*listenAddrApi)
	// }()

	var signalChan = make(chan os.Signal, 1)
	signal.Notify(signalChan, os.Interrupt, syscall.SIGTERM)
	select {
	case <-signalChan:
		logger.Info().Msg("got an interrupt, exiting...")
	case err := <-errChan:
		if err != nil {
			logger.Error().Err(err).Msg("error while running api, exiting...")
		}
	}

}

// func server(listenAddr string) error {
// 	gin.SetMode(gin.ReleaseMode)

// 	router := gin.New()
// 	router.POST("/api/v1/data", postDataToKafka)

// 	// for debugging purpose
// 	for _, routeInfo := range router.Routes() {
// 		logger.Debug().
// 			Str("path", routeInfo.Path).
// 			Str("handler", routeInfo.Handler).
// 			Str("method", routeInfo.Method).
// 			Msg("registered routes")
// 	}

// 	return router.Run(listenAddr)
// }

// func postDataToKafka(ctx *gin.Context) {
// 	parent := context.Background()
// 	defer parent.Done()

// 	form := &struct {
// 		Text string `form:"text" json:"text"`
// 	}{}

// 	ctx.Bind(form)
// 	formInBytes, err := json.Marshal(form)
// 	if err != nil {
// 		ctx.JSON(http.StatusUnprocessableEntity, map[string]interface{}{
// 			"error": map[string]interface{}{
// 				"message": fmt.Sprintf("error while marshalling json: %s", err.Error()),
// 			},
// 		})

// 		ctx.Abort()
// 		return
// 	}

// 	err = kafka_producer.Push(parent, nil, formInBytes)
// 	if err != nil {
// 		ctx.JSON(http.StatusUnprocessableEntity, map[string]interface{}{
// 			"error": map[string]interface{}{
// 				"message": fmt.Sprintf("error while push message into kafka: %s", err.Error()),
// 			},
// 		})

// 		ctx.Abort()
// 		return
// 	}

// 	ctx.JSON(http.StatusOK, map[string]interface{}{
// 		"success": true,
// 		"message": "success push data into kafka",
// 		"data":    form,
// 	})
// }
