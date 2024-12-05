rabbitmqctl add_exchange exchange_event direct

rabbitmqctl add_queue queue_event

rabbitmqctl add_binding exchange_event queue_event --routing-key routing_key_event