CREATE TABLE users (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255),
                        first_name VARCHAR(100),
                        last_name VARCHAR(100),
                        phone_number VARCHAR(20),
                        avatar_url TEXT,
                        role VARCHAR(20) NOT NULL,
                        provider VARCHAR(20),
                        provider_id VARCHAR(255),
                        is_verified BOOLEAN NOT NULL DEFAULT false,
                        is_active BOOLEAN NOT NULL DEFAULT true,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        last_login_at TIMESTAMP
 );

 CREATE TABLE categories (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL UNIQUE,
                             description VARCHAR(255),
                             image_url TEXT,
                             icon TEXT,
                             is_active BOOLEAN NOT NULL DEFAULT true,
                             is_featured BOOLEAN NOT NULL DEFAULT false,
                             sort_order INT,
                             parent_id BIGINT,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
 );


 CREATE TABLE products (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           short_description VARCHAR(1000),
                           sku VARCHAR(100) NOT NULL UNIQUE,
                           price DECIMAL(10, 2) NOT NULL,
                           compare_at_price DECIMAL(10, 2),
                           stock_quantity INT NOT NULL DEFAULT 0,
                           is_featured BOOLEAN NOT NULL DEFAULT false,
                           is_active BOOLEAN NOT NULL DEFAULT true,
                           weight DECIMAL(8, 3),
                           dimensions VARCHAR(255),
                           tags VARCHAR(255),
                           category_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (category_id) REFERENCES categories(id)
 );

 CREATE TABLE product_variants (
                                   id BIGSERIAL PRIMARY KEY,
                                   product_id BIGINT NOT NULL,
                                   sku VARCHAR(100) NOT NULL UNIQUE,
                                   variant_type VARCHAR(50) NOT NULL,
                                   variant_value VARCHAR(100) NOT NULL,
                                   variant_description VARCHAR(255),
                                   price DECIMAL(10, 2) NOT NULL,
                                   compare_at_price DECIMAL(10, 2),
                                   stock_quantity INT NOT NULL,
                                   is_active BOOLEAN NOT NULL DEFAULT true,
                                   sort_order INT,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
 );


 CREATE TABLE product_images (
                                id BIGSERIAL PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                image_url TEXT NOT NULL,
                                alt_text TEXT,
                                is_primary BOOLEAN NOT NULL DEFAULT false,
                                sort_order INT NOT NULL DEFAULT 0,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);


 CREATE TABLE reviews (
                          id BIGSERIAL PRIMARY KEY,
                          product_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                          title VARCHAR(1000),
                          comment TEXT,
                          is_verified_purchase BOOLEAN NOT NULL DEFAULT false,
                          status VARCHAR(20) NOT NULL,
                          is_featured BOOLEAN NOT NULL DEFAULT false,
                          helpful_votes INT DEFAULT 0,
                          total_votes INT DEFAULT 0,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
 );


 CREATE TABLE addresses (
                            id BIGSERIAL PRIMARY KEY,
                            first_name VARCHAR(100),
                            last_name VARCHAR(100),
                            company VARCHAR(100),
                            address_line_1 VARCHAR(255),
                            address_line_2 VARCHAR(255),
                            city VARCHAR(100),
                            state VARCHAR(100),
                            postal_code VARCHAR(20),
                            country VARCHAR(100),
                            phone VARCHAR(20)
 );


 CREATE TABLE orders (
                         id BIGSERIAL PRIMARY KEY,
                         order_number VARCHAR(50) NOT NULL UNIQUE,
                         user_id BIGINT NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         subtotal DECIMAL(10, 2) NOT NULL,
                         tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                         shipping_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                         discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                         total_amount DECIMAL(10, 2) NOT NULL,
                         currency VARCHAR(10) NOT NULL,
                         payment_status VARCHAR(20),
                         payment_method VARCHAR(50),
                         payment_intent_id VARCHAR(100),
                         payment_date TIMESTAMP,
                         shipping_method VARCHAR(100),
                         tracking_number VARCHAR(100),
                         shipped_date TIMESTAMP,
                         delivered_date TIMESTAMP,
                         coupon_code VARCHAR(50),
                         shipping_address_id BIGINT,
                         billing_address_id BIGINT,
                         notes TEXT,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
                         FOREIGN KEY (shipping_address_id) REFERENCES addresses(id) ON DELETE SET NULL,
                         FOREIGN KEY (billing_address_id) REFERENCES addresses(id) ON DELETE SET NULL
 );

 CREATE TABLE order_items (
                              id BIGSERIAL PRIMARY KEY,
                              order_id BIGINT NOT NULL,
                              product_id BIGINT NOT NULL,
                              product_variant_id BIGINT,
                              product_name VARCHAR(255),
                              product_sku VARCHAR(100),
                              product_image_url TEXT,
                              variant_name VARCHAR(255),
                              quantity INT NOT NULL,
                              unit_price DECIMAL(10, 2) NOT NULL,
                              discount_amount DECIMAL(10, 2),
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                              FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
                              FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE SET NULL
 );

 CREATE TABLE cart_items (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_variant_id BIGINT,
                             quantity INT NOT NULL,
                             added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE SET NULL
 );

  CREATE INDEX idx_user_email ON users(email);
  CREATE UNIQUE INDEX idx_user_provider_id ON users(provider_id);
  CREATE INDEX idx_user_provider ON users(provider);
  CREATE INDEX idx_category_parent ON categories(parent_id);
  CREATE INDEX idx_category_name ON categories(name);
  CREATE INDEX idx_category_active ON categories(is_active);
  CREATE INDEX idx_product_category ON products(category_id);
  CREATE INDEX idx_product_name ON products(name);
  CREATE INDEX idx_product_price ON products(price);
  CREATE INDEX idx_product_active ON products(is_active);
  CREATE INDEX idx_variant_product ON product_variants(product_id);
  CREATE INDEX idx_variant_sku ON product_variants(sku);
  CREATE INDEX idx_variant_type ON product_variants(variant_type);
  CREATE INDEX idx_variant_active ON product_variants(is_active);
  CREATE INDEX idx_image_product ON product_images(product_id);
  CREATE INDEX idx_review_product ON reviews(product_id);
  CREATE INDEX idx_review_user ON reviews(user_id);
  CREATE INDEX idx_review_rating ON reviews(rating);
  CREATE INDEX idx_review_status ON reviews(status);
  CREATE INDEX idx_review_created ON reviews(created_at);
  CREATE INDEX idx_order_user ON orders(user_id);
  CREATE INDEX idx_order_status ON orders(status);
  CREATE INDEX idx_order_created ON orders(created_at);
  CREATE INDEX idx_order_number ON orders(order_number);
  CREATE INDEX idx_order_item_order ON order_items(order_id);
  CREATE INDEX idx_cart_user ON cart_items(user_id);
  CREATE INDEX idx_cart_product ON cart_items(product_id);
  CREATE INDEX idx_cart_variant ON cart_items(product_variant_id);


-- ================= Insert main categories =================
INSERT INTO categories (name, description, image_url, icon, is_active, is_featured, sort_order, created_at, updated_at)
VALUES
    ('Men''s Shoes', 'Fashion footwear for men', '/images/Category/Men.webp', 'fas fa-shoe-prints', TRUE, TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Women''s Shoes', 'Fashion footwear for women', '/images/Category/Woman.jpg', 'fas fa-shoe-prints', TRUE, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Sports Shoes', 'Athletic and sports footwear', '/images/Category/Sport.webp', 'fas fa-running', TRUE, TRUE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Slippers & Sandals', 'Casual slippers and sandals', '/images/Category/Sandal.webp', 'fas fa-socks', TRUE, FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Kids Shoes', 'Footwear for children', '/images/Category/Kid.jpg', 'fas fa-child', TRUE, FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



-- ================= Insert subcategories =================
WITH subcategories_data(parent_name, name, description, image_url, icon, is_active, is_featured, sort_order) AS (
    VALUES
        ('Men''s Shoes', 'Dress Shoes', 'Elegant formal shoes for office', '/images/Category/Men-Dress.webp', 'fas fa-briefcase', TRUE, TRUE, 1),
        ('Men''s Shoes', 'Loafers', 'Comfortable slip-on shoes', '/images/Category/Loafers.jpg', 'fas fa-shoe-prints', TRUE, TRUE, 2)
)
INSERT INTO categories (name, description, image_url, icon, parent_id, is_active, is_featured, sort_order, created_at, updated_at)
SELECT sd.name, sd.description, sd.image_url, sd.icon, p.id, sd.is_active, sd.is_featured, sd.sort_order, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM subcategories_data sd
         JOIN categories p ON p.name = sd.parent_name;

WITH men_products AS (
    INSERT INTO products (
                          name, description, short_description, price, stock_quantity, sku,
                          is_active, is_featured, created_at, updated_at, category_id
        )
        VALUES
            ('Classic Leather Oxford', 'Premium leather oxford shoes for formal occasions', 'Elegant formal shoes', 149.99, 50, 'MEN-OXFORD-001', true, true, NOW(), NOW(), 1),
            ('Casual Sneakers', 'Comfortable sneakers for daily wear', 'Stylish casual sneakers', 89.99, 75, 'MEN-SNEAKER-002', true, false, NOW(), NOW(), 1),
            ('Running Shoes', 'High-performance running shoes with cushioning', 'Lightweight running shoes', 119.99, 60, 'MEN-RUN-003', true, false, NOW(), NOW(), 3),
            ('Leather Loafers', 'Classic leather loafers for a smart look', 'Comfortable slip-on loafers', 109.99, 45, 'MEN-LOAFER-004', true, false, NOW(), NOW(), 1),
            ('Athletic Trainers', 'Versatile trainers for gym and casual wear', 'Supportive athletic shoes', 99.99, 65, 'MEN-TRAINER-005', true, true, NOW(), NOW(), 3),
            ('Casual Slip-ons', 'Easy-to-wear slip-on shoes', 'Convenient slip-on design', 79.99, 55, 'MEN-SLIPON-006', true, false, NOW(), NOW(), 1),
            ('Hiking Boots', 'Durable boots for outdoor adventures', 'Waterproof hiking boots', 159.99, 30, 'MEN-HIKE-007', true, true, NOW(), NOW(), 3),
            ('Dress Shoes', 'Elegant dress shoes for special occasions', 'Shiny formal shoes', 129.99, 40, 'MEN-DRESS-008', true, false, NOW(), NOW(), 1),
            ('Canvas Sneakers', 'Lightweight canvas sneakers', 'Breathable summer shoes', 69.99, 70, 'MEN-CANVAS-009', true, false, NOW(), NOW(), 1),
            ('Leather Boots', 'Stylish leather boots for men', 'Classic ankle boots', 139.99, 35, 'MEN-BOOT-010', true, false, NOW(), NOW(), 1)
        ON CONFLICT (sku) DO NOTHING
        RETURNING id, sku
)
INSERT INTO product_images (image_url, alt_text, is_primary, sort_order, product_id, created_at)
SELECT
    CASE sku
        WHEN 'MEN-OXFORD-001' THEN '/images/Mens/image (1).jpg'
        WHEN 'MEN-SNEAKER-002' THEN '/images/Mens/image (2).jpg'
        WHEN 'MEN-RUN-003' THEN '/images/Mens/image (3).webp'
        WHEN 'MEN-LOAFER-004' THEN '/images/Mens/image (4).webp'
        WHEN 'MEN-TRAINER-005' THEN '/images/Mens/image (5).webp'
        WHEN 'MEN-SLIPON-006' THEN '/images/Mens/image (6).jpg'
        WHEN 'MEN-HIKE-007' THEN '/images/Mens/image (7).jpg'
        WHEN 'MEN-DRESS-008' THEN '/images/Mens/image (8).jpg'
        WHEN 'MEN-CANVAS-009' THEN '/images/Mens/image (9).jpg'
        WHEN 'MEN-BOOT-010' THEN '/images/Mens/image (10).jpg'
        END AS image_url,
    CONCAT(sku, ' - Main Image') AS alt_text,
    true, 1, id, NOW()
FROM men_products;



WITH women_products AS (
    INSERT INTO products (
                          name, description, short_description, price, stock_quantity, sku,
                          is_active, is_featured, created_at, updated_at, category_id
        )
        VALUES
            ('Elegant Heels', 'Stylish high heels for special occasions', 'Classic black heels', 129.99, 40, 'WOM-HEEL-101', true, true, NOW(), NOW(), 2),
            ('Comfort Flats', 'Comfortable ballet flats for daily wear', 'Versatile black flats', 79.99, 60, 'WOM-FLAT-102', true, false, NOW(), NOW(), 2),
            ('Running Sneakers', 'Lightweight running shoes for women', 'Performance running shoes', 109.99, 50, 'WOM-RUN-103', true, false, NOW(), NOW(), 3),
            ('Ankle Boots', 'Stylish ankle boots for fall/winter', 'Warm winter boots', 119.99, 35, 'WOM-BOOT-104', true, true, NOW(), NOW(), 2),
            ('Summer Sandals', 'Comfortable summer sandals', 'Breathable open-toe sandals', 59.99, 70, 'WOM-SANDAL-105', true, false, NOW(), NOW(), 4),
            ('Casual Slip-ons', 'Easy-to-wear slip-on shoes', 'Casual everyday shoes', 69.99, 65, 'WOM-SLIPON-106', true, false, NOW(), NOW(), 2),
            ('Athletic Trainers', 'Comfortable training shoes', 'Supportive workout shoes', 89.99, 55, 'WOM-TRAINER-107', true, true, NOW(), NOW(), 3),
            ('Leather Loafers', 'Classic leather loafers', 'Smart casual shoes', 99.99, 45, 'WOM-LOAFER-108', true, false, NOW(), NOW(), 2),
            ('Comfort Wedges', 'Stylish yet comfortable wedges', 'Casual wedge sandals', 89.99, 40, 'WOM-WEDGE-109', true, false, NOW(), NOW(), 2),
            ('Fashion Sneakers', 'Trendy sneakers for women', 'Stylish casual sneakers', 79.99, 60, 'WOM-SNEAKER-110', true, false, NOW(), NOW(), 2)
        ON CONFLICT (sku) DO NOTHING
        RETURNING id, sku
)
INSERT INTO product_images (image_url, alt_text, is_primary, sort_order, product_id, created_at)
SELECT
    CASE sku
        WHEN 'WOM-HEEL-101' THEN '/images/Woman/image (1).jpeg'
        WHEN 'WOM-FLAT-102' THEN '/images/Woman/image (2).jpg'
        WHEN 'WOM-RUN-103' THEN '/images/Woman/image (3).webp'
        WHEN 'WOM-BOOT-104' THEN '/images/Woman/image (4).jpg'
        WHEN 'WOM-SANDAL-105' THEN '/images/Woman/image (5).jpg'
        WHEN 'WOM-SLIPON-106' THEN '/images/Woman/image (6).jpg'
        WHEN 'WOM-TRAINER-107' THEN '/images/Woman/image (7).jpg'
        WHEN 'WOM-LOAFER-108' THEN '/images/Woman/image (8).jpg'
        WHEN 'WOM-WEDGE-109' THEN '/images/Woman/image (9).webp'
        WHEN 'WOM-SNEAKER-110' THEN '/images/Woman/image (10).webp'
        END AS image_url,
    CONCAT(sku, ' - Main Image') AS alt_text,
    true, 1, id, NOW()
FROM women_products;


WITH kids_products AS (
    INSERT INTO products (
                          name, description, short_description, price, stock_quantity, sku,
                          is_active, is_featured, created_at, updated_at, category_id
        )
        VALUES
            ('Children''s Sneakers', 'Comfortable sneakers for active kids', 'Colorful kids'' shoes', 49.99, 40, 'KID-SNEAKER-201', true, true, NOW(), NOW(), 5),
            ('Kids'' Sandals', 'Breathable sandals for summer', 'Lightweight kids'' sandals', 34.99, 55, 'KID-SANDAL-202', true, false, NOW(), NOW(), 4),
            ('School Shoes', 'Durable shoes for school', 'Comfortable school shoes', 44.99, 50, 'KID-SCHOOL-203', true, false, NOW(), NOW(), 5),
            ('Toddler Sneakers', 'First walking shoes for toddlers', 'Soft-soled baby shoes', 39.99, 45, 'KID-TODDLER-204', true, false, NOW(), NOW(), 5),
            ('Kids'' Rain Boots', 'Waterproof boots for rainy days', 'Colorful rain boots', 42.99, 35, 'KID-RAIN-205', true, true, NOW(), NOW(), 5),
            ('Kids'' Slip-ons', 'Easy-to-wear shoes for kids', 'Convenient slip-on design', 37.99, 60, 'KID-SLIPON-206', true, false, NOW(), NOW(), 5)
        ON CONFLICT (sku) DO NOTHING
        RETURNING id, sku
)
INSERT INTO product_images (image_url, alt_text, is_primary, sort_order, product_id, created_at)
SELECT
    CASE sku
        WHEN 'KID-SNEAKER-201' THEN '/images/Kid/image (1).jpg'
        WHEN 'KID-SANDAL-202' THEN '/images/Kid/image (2).jpg'
        WHEN 'KID-SCHOOL-203' THEN '/images/Kid/image (3).jpg'
        WHEN 'KID-TODDLER-204' THEN '/images/Kid/image (4).jpg'
        WHEN 'KID-RAIN-205' THEN '/images/Kid/image (5).jpg'
        WHEN 'KID-SLIPON-206' THEN '/images/Kid/image (6).webp'
        END AS image_url,
    CONCAT(sku, ' - Main Image') AS alt_text,
    true, 1, id, NOW()
FROM kids_products;