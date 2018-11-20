START TRANSACTION;

DROP TABLE order_table;

CREATE TABLE order_table (id BIGINT, 
				order_state varchar(200), 
				PRIMARY KEY (id));
				
CREATE TABLE compute_order_table (id BIGINT,  
				PRIMARY KEY (id),
				foreign key (id) references order_table);	
				
CREATE TABLE network_order_table (id BIGINT,  
				PRIMARY KEY (id),
				foreign key (id) references order_table);	
				
CREATE TABLE volume_order_table (id BIGINT,  
				PRIMARY KEY (id),
				foreign key (id) references order_table);
				
CREATE TABLE attachment_order_table (id BIGINT,  
				PRIMARY KEY (id),
				foreign key (id) references order_table);
				
CREATE TABLE public_ip_order_table (id BIGINT,  
				PRIMARY KEY (id),
				foreign key (id) references order_table);																				
     
-- orders --
INSERT INTO order_table (id, order_state) VALUES (1, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (2, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (3, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (4, 'FULFILLED');		
INSERT INTO order_table (id, order_state) VALUES (5, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (6, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (7, 'FAILED_AFTER_SUCCESSUL_REQUEST');
INSERT INTO order_table (id, order_state) VALUES (8, 'FAILED_ON_REQUEST');
INSERT INTO order_table (id, order_state) VALUES (9, 'FAILED_AFTER_SUCCESSUL_REQUEST');
INSERT INTO order_table (id, order_state) VALUES (10, 'FAILED_ON_REQUEST');

INSERT INTO order_table (id, order_state) VALUES (11, 'FULFILLED');
INSERT INTO order_table (id, order_state) VALUES (12, 'FULFILLED');

INSERT INTO order_table (id, order_state) VALUES (13, 'FAILED_ON_REQUEST');
INSERT INTO order_table (id, order_state) VALUES (14, 'FAILED_ON_REQUEST');

-- computes --
INSERT INTO compute_order_table (id) VALUES (1);
INSERT INTO compute_order_table (id) VALUES (2);
INSERT INTO compute_order_table (id) VALUES (3);
INSERT INTO compute_order_table (id) VALUES (7);
INSERT INTO compute_order_table (id) VALUES (8);

-- volumes --
INSERT INTO volume_order_table (id) VALUES (4);
INSERT INTO volume_order_table (id) VALUES (5);
INSERT INTO volume_order_table (id) VALUES (9);

-- network --
INSERT INTO network_order_table (id) VALUES (6);
INSERT INTO network_order_table (id) VALUES (10);

-- attachment --
INSERT INTO attachment_order_table (id) VALUES (11);
INSERT INTO attachment_order_table (id) VALUES (13);

-- public ip --
INSERT INTO public_ip_order_table (id) VALUES (12);
INSERT INTO public_ip_order_table (id) VALUES (14);
