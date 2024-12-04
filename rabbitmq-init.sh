rabbitmqctl add_exchange exchange_event direct

rabbitmqctl add_queue queue_event

rabbitmqctl add_binding my_exchange my_queue --routing-key routing_key_event