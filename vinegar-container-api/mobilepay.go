package main

import (
	"fmt"
	"github.com/imroc/req"
)

type Binding struct {
	ReceiverAlias       string `json:"receiverAlias"`
	MerchantId          string `json:"merchantId"`
	MerchantBinding     string `json:"merchantBinding"`
	MerchantServiceName string `json:"merchantServiceName"`
}

type Payment struct {
	MerchantId            string  `json:"merchantId"`
	MerchantBinding       string  `json:"merchantBinding"`
	ReceiverRegNumber     string  `json:"receiverRegNumber"`
	ReceiverAccountNumber string  `json:"receiverAccountNumber"`
	Amount                float32 `json:"amount"`
}

var header = req.Header{
	"x-ibm-client-id":     "1c0cd3ff-1143-476b-b136-efe9b1f5ecf3",
	"x-ibm-client-secret": "L7yW0eV0eK5yX1nK4rO0lI8sX5aN2tL6aQ0sL7gM1xO6sW8kK1",
}

func bind() {
	body := Binding{
		ReceiverAlias:       "+4568469919",
		MerchantId:          "30b6bd05-c0a9-4e6b-8ee5-1d66b5987c5a",
		MerchantBinding:     "000000-0000",
		MerchantServiceName: "Donation",
	}
	url := "https://api.sandbox.mobilepay.dk/bindings-restapi/api/v1/bindings"
	if _, err := req.Post(url, header, req.BodyJSON(&body)); err != nil {
		fmt.Sprintln(err.Error())
	}
}

func pay(amount float32) {
	body := Payment{
		MerchantId:            "30b6bd05-c0a9-4e6b-8ee5-1d66b5987c5a",
		MerchantBinding:       "000000-0000",
		ReceiverRegNumber:     "3098",
		ReceiverAccountNumber: "3100460793",
		Amount:                amount,
	}
	url := "https://api.sandbox.mobilepay.dk/bindings-restapi/api/v1/payments/payout-bankaccount"
	if _, err := req.Post(url, header, req.BodyJSON(&body)); err != nil {
		fmt.Sprintln(err.Error())
	}
}
