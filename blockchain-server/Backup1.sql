-- Inserting roles, assuming these are new and there are no conflicts with existing IDs.
INSERT INTO roles(name) VALUES('ROLE_CUSTOMER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
INSERT INTO roles(name) VALUES('ROLE_STAFF');
INSERT INTO roles(name) VALUES('CONTROLLER');

-- Inserting users
-- Replace 'password_hash', 'address', 'ci_card' with actual hashed passwords, addresses, and CI cards respectively.
INSERT INTO users(
    email, first_name, is_active, last_name, password, phone_number, username,balance)
    VALUES 
    ('user@example.com', 'UserFirstName', true, 'UserLastName', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '0000000000', 'user',0.0),
    ('staff@example.com', 'StaffFirstName', true, 'StaffLastName', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '1111111111', 'staff',0.0),
    ('admin@example.com', 'AdminFirstName', true, 'AdminLastName', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '2222222222', 'admin',0.0),
	('customer1@gmail.com', 'John', true, 'Doe', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '3333333333', 'john_doe', 0.0),
	('customer2@gmail.com', 'Jane', true, 'Smith', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '4444444444', 'jane_smith', 0.0),
	('customer3@gmail.com', 'Michael', true, 'Johnson', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '5555555555', 'michael_johnson', 0.0),
	('customer4@gmail.com', 'Emily', true, 'Williams', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '6666666666', 'emily_williams', 0.0),
	('customer5@gmail.com', 'David', true, 'Brown', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '7777777777', 'david_brown', 0.0),
	('customer6@gmail.com', 'Sarah', true, 'Miller', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '8888888888', 'sarah_miller', 0.0),
	('customer7@gmail.com', 'Daniel', true, 'Taylor', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '9999999999', 'daniel_taylor', 0.0),
	('customer8@gmail.com', 'Olivia', true, 'Anderson', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '1010101010', 'olivia_anderson', 0.0),
	('customer9@gmail.com', 'James', true, 'Wilson', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '1111111111', 'james_wilson', 0.0),
	('customer10@gmail.com', 'Emma', true, 'Martinez', '$2a$10$Nfdtc9HNPo/NKTMNRbfA2.BkBv5Nf8RmptlHcJk4X9W0kRudJXNt.', '1212121212', 'emma_martinez', 0.0);

-- Inserting user information with all avatars set to null
INSERT INTO user_info(
    address, ci_card, dob, first_name, last_name, phone_number, user_id)
    VALUES 
    ('UserAddress', NULL, '2001-04-24' , 'UserFirstName', 'UserLastName', '000-000-0000', 1),
  	('StaffAddress', NULL, '2001-04-24', 'StaffFirstName', 'StaffLastName', '111-111-1111', 2),
  	('AdminAddress', NULL, '2001-04-24', 'AdminFirstName', 'AdminLastName', '222-222-2222', 3),
  	('John''s Address', NULL, '2001-04-24', 'John', 'Doe', '333-333-3333', 4),
  	('Jane''s Address', NULL, '2001-04-24', 'Jane', 'Smith', '444-444-4444', 5),
  	('Michael''s Address', NULL, '2001-04-24', 'Michael', 'Johnson', '555-555-5555', 6),
  	('Emily''s Address', NULL, '2001-04-24', 'Emily', 'Williams', '666-666-6666', 7),
  	('David''s Address', NULL, '2001-04-24', 'David', 'Brown', '777-777-7777', 8),
  	('Sarah''s Address', NULL, '2001-04-24', 'Sarah', 'Miller', '888-888-8888', 9),
  	('Daniel''s Address', NULL, '2001-04-24', 'Daniel', 'Taylor', '999-999-9999', 10),
  	('Olivia''s Address', NULL, '2001-04-24', 'Olivia', 'Anderson', '1010-1010-10', 11),
  	('James''s Address', NULL, '2001-04-24', 'James', 'Wilson', '111-111-1111', 12),
  	('Emma''s Address', NULL, '2001-04-24', 'Emma', 'Martinez', '1212-1212-12', 13);
-- Assigning roles to users
INSERT INTO user_roles(user_id, role_id)
    VALUES 
   	(1, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(2, (SELECT id FROM roles WHERE name = 'ROLE_STAFF')),
	(3, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
    (4, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(5, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(6, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(7, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(8, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(9, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(10, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(11, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(12, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
	(13, (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER'));
INSERT INTO categories(category_name) VALUES 
('BRACELETS'),
('RINGS'),
('NECKLACES'),
('EARRINGS'),
('ENGAGEMENT RINGS'),
('WEDDING BANDS');
-- product needed
INSERT INTO products (description, price, product_name, categories_id,unit_of_stock) VALUES
('Exquisite 24K gold necklace with a durable clasp, 20 inches in length.', 850.00, 'Luxury Gold Necklace', 3,50),
('Hand-forged 24K gold earrings, inspired by ancient Grecian art.', 400.00, 'Grecian Gold Earrings', 4,50),
('Elegant 18K gold bracelet with diamond accents and a sophisticated design.', 1200.00, 'Gold Diamond Bracelet', 1,50),
('Exclusive 22K gold ring featuring intricate filigree work.', 950.00, 'Filigree Gold Ring', 2,50),
('Sophisticated 18K gold engagement ring with a solitaire diamond.', 3000.00, 'Solitaire Engagement Ring', 5,50),
('Classic 18K gold wedding band with a comfortable fit and brushed finish.', 700.00, 'Classic Gold Wedding Band', 6,50),
('Modern 14K gold stackable rings with a polished finish.', 500.00, 'Stackable Gold Rings', 2,50),
('Elegant pearl and 18K gold necklace, perfect for evening wear.', 1300.00, 'Pearl Gold Necklace', 3,50),
('Stunning 24K gold hoop earrings for a bold statement.', 650.00, 'Bold Gold Hoop Earrings', 4,50),
('Delicate 18K gold ankle bracelet for a touch of summer elegance.', 550.00, 'Gold Ankle Bracelet', 1,50),
('Vintage-inspired 18K gold and sapphire ring, a timeless piece.', 1200.00, 'Sapphire Gold Ring', 2,50),
('Art Deco 18K gold earrings with diamond and emerald accents.', 2200.00, 'Emerald Deco Earrings', 4,50),
('Handcrafted 22K gold pendant necklace, a unique piece.', 1600.00, 'Handcrafted Gold Pendant', 3,50),
('Classic solitaire diamond engagement ring in 18K gold setting.', 3200.00, 'Classic Solitaire Ring', 5,50),
('Matching 18K gold wedding bands with personalized engraving option.', 1400.00, 'Personalized Wedding Bands', 6,50),
('Contemporary 18K white gold engagement ring with pavé diamonds.', 3750.00, 'Pavé Diamond Engagement Ring', 5,50);
-- forums
INSERT INTO forums(is_hide)
	VALUES (false);
-- Inserting categories

INSERT INTO category_forums (category_name, forum_id)
VALUES 
('Gold Products', 1),
('Digital Gold', 1);
-- Inserting posts
INSERT INTO posts (content, create_date, delete_date, is_hide, title, update_date, category_post_id, user_id, text_img, is_pinned)
VALUES 
('Discussing the latest trends in gold jewelry.', '2024-03-12', null, false, 'Trends in Gold Jewelry', '2024-03-12', 1, 1, null, false),
('Exploring the benefits of investing in digital gold.', '2024-03-12', null, false, 'Benefits of Digital Gold', '2024-03-12', 2, 1, null, true),
('Share your favorite gold jewelry pieces and why you love them!', '2024-03-12', null, false, 'Favorite Gold Jewelry', '2024-03-12', 1, 2, null, true),
('Tips for buying and selling digital gold safely and securely.', '2024-03-12', null, false, 'Buying and Selling Digital Gold', '2024-03-12', 2, 2, null,false),
('Looking for recommendations for gold earrings. Any suggestions?', '2024-03-12', null, false, 'Recommendations for Gold Earrings', '2024-03-12', 1, 3, null, true),
('Discussion on the future of digital gold and its potential impact on the market.', '2024-03-12', null, false, 'Future of Digital Gold', '2024-03-12', 2, 3, null, false),
('Show off your gold bracelet collection and inspire others!', '2024-03-12', null, false, 'Gold Bracelet Collection Showcase', '2024-03-12', 1, 4, null, false),
('How to store digital gold securely to protect your investment.', '2024-03-12', null, false, 'Storing Digital Gold Safely', '2024-03-12', 2, 4, null, false),
('Discussing the craftsmanship behind gold rings and their significance.', '2024-03-12', null, false, 'Craftsmanship of Gold Rings', '2024-03-12', 1, 5, null, false),
('Understanding the differences between physical gold and digital gold.', '2024-03-12', null, false, 'Physical vs Digital Gold', '2024-03-12', 2, 5, null, true);

INSERT INTO rates (content, create_date, delete_date, is_hide, update_date, post_id, user_id, img_url)
VALUES 
('This necklace is stunning! I love the intricate design and the quality of the gold.', '2024-03-12', null, false, '2024-03-12', 1, 1, null),
('Absolutely gorgeous earrings! They look even better in person.', '2024-03-12', null, false, '2024-03-12', 2, 2, null),
('The diamond bracelet is exquisite! It adds a touch of elegance to any outfit.', '2024-03-12', null, false, '2024-03-12', 3, 3, null),
('I adore this filigree ring! The craftsmanship is top-notch.', '2024-03-12', null, false, '2024-03-12', 4, 4, null),
('This engagement ring is a dream come true! It''s perfect in every way.', '2024-03-12', null, false, '2024-03-12', 5, 5, null),
('The wedding band is classic and timeless. I couldn''t be happier with it.', '2024-03-12', null, false, '2024-03-12', 6, 6, null),
('These stackable rings are so versatile! I wear them every day.', '2024-03-12', null, false, '2024-03-12', 7, 7, null),
('The pearl necklace is absolutely stunning! It''s perfect for special occasions.', '2024-03-12', null, false, '2024-03-12', 8, 8, null),
('These hoop earrings are a showstopper! I get compliments every time I wear them.', '2024-03-12', null, false, '2024-03-12', 9, 9, null),
('The ankle bracelet is delicate and beautiful. It''s the perfect summer accessory.', '2024-03-12', null, false, '2024-03-12', 1, 10, null),
('This sapphire ring is breathtaking! The color of the stone is stunning.', '2024-03-12', null, false, '2024-03-12', 1, 11, null),
('I''m in love with these deco earrings! They''re even more gorgeous in person.', '2024-03-12', null, false, '2024-03-12', 2, 12, null),
('The handcrafted pendant necklace is unique and beautiful. I wear it every day.', '2024-03-12', null, false, '2024-03-12', 3, 13, null)

INSERT INTO discounts (code, date_create, date_expire, is_expire, percentage)
VALUES 
('ubfz2B6i', '2024-03-13T22:39:00.9137706', '2024-03-25T23:30:00', false, 3.0),
('kvhnuQQ5', '2024-03-12T19:24:00.2467706', '2024-03-20T21:21:00', false, 5.0),
('thanhliq', '2024-03-14T09:00:00', '2024-03-15T23:59:00', false, 5.0),
('freeship', '2024-03-14T10:00:00', '2024-04-01T00:00:00', false, 10.0),
('flashsal', '2024-03-14T12:00:00', '2024-03-14T23:59:00', false, 4.0),
('member15', '2024-03-14T13:00:00', '2024-03-31T23:59:00', true, 15.0)

INSERT INTO discount_code_user(is_valid, discount_id, user_id)
VALUES 
(false, 1, 4),
(false, 2, 4),
(true, 5, 4),
(false, 1, 5),
(false, 3, 5),
(false, 4, 6),
(false, 6, 6)

INSERT INTO carts(user_id)
VALUES 
(1),
(4),
(5),
(6)

INSERT INTO cart_items(price, quantity, amount, cart_id, product_id)
VALUES 
(850.00, 2, 1700.00, 2, 1),
(400.00, 4, 1600.00, 2, 2),
(1200.00,1, 1200.00, 3, 3),
(700.00, 5, 3500.00, 4, 6)

INSERT INTO orders(address,buy_or_sell,create_date,discount_code,email,first_name,last_name,phone_number,product_type,qr_code,status,status_received,total_amount,user_id)
VALUES 
('John''s Address', 'BUY', '2024-03-16', 'flashsal', 'customer1@gmail.com', 'John', 'Doe', '3333333333', 'CRAFTED_GOLD', 'hwkB3nsm', 'UNPAID', 'NOT_RECEIVED', 5136.00,4)

INSERT INTO order_detail(price,quantity,amount,is_consigment,order_id,product_id)
VALUES
(950.00, 3, 2850.00, false, 1, 4),
(500.00, 5, 2500.00, false, 1, 7)