


CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       phone VARCHAR(20),
                       avatar TEXT,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       provider VARCHAR(20),
                       provider_id VARCHAR(255),
                       email_verified BOOLEAN NOT NULL DEFAULT false,
                       active BOOLEAN NOT NULL DEFAULT true,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            slug VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            image_url TEXT,
                            parent_id BIGINT,
                            active BOOLEAN NOT NULL DEFAULT true,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
);


CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) NOT NULL UNIQUE,
                          description TEXT,
                          short_description TEXT,
                          price DECIMAL(10, 2) NOT NULL,
                          compare_at_price DECIMAL(10, 2),
                          cost_per_item DECIMAL(10, 2),
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          barcode VARCHAR(100),
                          quantity INT NOT NULL DEFAULT 0,
                          is_taxable BOOLEAN NOT NULL DEFAULT true,
                          is_featured BOOLEAN NOT NULL DEFAULT false,
                          is_active BOOLEAN NOT NULL DEFAULT true,
                          category_id BIGINT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_variants (
                                  id BIGSERIAL PRIMARY KEY,
                                  product_id BIGINT NOT NULL,
                                  name VARCHAR(255) NOT NULL,
                                  sku VARCHAR(100) NOT NULL UNIQUE,
                                  barcode VARCHAR(100),
                                  price DECIMAL(10, 2) NOT NULL,
                                  compare_at_price DECIMAL(10, 2),
                                  cost_per_item DECIMAL(10, 2),
                                  quantity INT NOT NULL DEFAULT 0,
                                  weight DECIMAL(10, 2),
                                  is_active BOOLEAN NOT NULL DEFAULT true,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);


CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         product_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                         title VARCHAR(255),
                         comment TEXT,
                         is_approved BOOLEAN NOT NULL DEFAULT false,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           company VARCHAR(100),
                           address1 TEXT NOT NULL,
                           address2 TEXT,
                           city VARCHAR(100) NOT NULL,
                           province VARCHAR(100),
                           country VARCHAR(100) NOT NULL,
                           zip_code VARCHAR(20) NOT NULL,
                           phone VARCHAR(20) NOT NULL,
                           is_default BOOLEAN NOT NULL DEFAULT false,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(50) NOT NULL UNIQUE,
                        user_id BIGINT,
                        status VARCHAR(20) NOT NULL,
                        subtotal DECIMAL(10, 2) NOT NULL,
                        tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        shipping_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        total DECIMAL(10, 2) NOT NULL,
                        payment_status VARCHAR(20) NOT NULL,
                        payment_method VARCHAR(50),
                        shipping_address_id BIGINT,
                        billing_address_id BIGINT,
                        notes TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                        FOREIGN KEY (shipping_address_id) REFERENCES addresses(id) ON DELETE SET NULL,
                        FOREIGN KEY (billing_address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT,
                             variant_id BIGINT,
                             product_name VARCHAR(255) NOT NULL,
                             variant_name VARCHAR(255),
                             sku VARCHAR(100) NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,
                             quantity INT NOT NULL,
                             total_price DECIMAL(10, 2) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
                             FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL
);

CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            variant_id BIGINT,
                            quantity INT NOT NULL DEFAULT 1,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                            FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_provider ON users(provider, provider_id);
CREATE INDEX idx_category_parent ON categories(parent_id);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_slug ON products(slug);
CREATE INDEX idx_variant_product ON product_variants(product_id);
CREATE INDEX idx_variant_sku ON product_variants(sku);
CREATE INDEX idx_image_product ON product_images(product_id);
CREATE INDEX idx_review_product ON reviews(product_id);
CREATE INDEX idx_review_user ON reviews(user_id);
CREATE INDEX idx_address_user ON addresses(user_id);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_order_item_order ON order_items(order_id);
CREATE INDEX idx_cart_user ON cart_items(user_id);


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