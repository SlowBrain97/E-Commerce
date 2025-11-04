# E-Commerce バックエンド

Spring Boot 3 と Java 21 で構築された、モダンなEコマースプラットフォームのバックエンドAPIです。

## 📋 目次

- [概要](#概要)
- [主な機能](#主な機能)
- [技術スタック](#技術スタック)
- [前提条件](#前提条件)
- [セットアップ](#セットアップ)
- [環境変数](#環境変数)
- [実行方法](#実行方法)
- [API ドキュメント](#api-ドキュメント)
- [データベースマイグレーション](#データベースマイグレーション)
- [プロジェクト構成](#プロジェクト構成)

## 概要

このプロジェクトは、エンタープライズグレードのEコマースアプリケーションのバックエンドAPIを提供します。RESTful API設計、セキュリティのベストプラクティス、スケーラブルなアーキテクチャを採用しています。

## 主な機能

- 🔐 **認証・認可**
  - JWT ベースの認証
  - OAuth2 統合（Google）
  - ロールベースのアクセス制御（RBAC）

- 🛒 **Eコマース機能**
  - 商品管理（CRUD操作）
  - カート機能
  - 注文処理
  - 在庫管理

- 💳 **決済統合**
  - Stripe 決済ゲートウェイ
  - セキュアな決済処理
  - Webhook サポート

- 📧 **通知システム**
  - メール通知（SMTP）
  - 注文確認メール
  - パスワードリセット機能

- 🚀 **パフォーマンス**
  - Redis キャッシング
  - データベース最適化
  - 非同期処理

- 📊 **監視・管理**
  - Spring Boot Actuator
  - ヘルスチェックエンドポイント
  - Prometheus メトリクス

## 技術スタック

### コアフレームワーク
- **Spring Boot** 3.2.0
- **Java** 21
- **Maven** - ビルドツール

### データベース
- **PostgreSQL** 15 - メインデータベース
- **Redis** 7 - キャッシュ層
- **Flyway** - データベースマイグレーション

### セキュリティ
- **Spring Security** - 認証・認可
- **JWT** (jjwt 0.11.5) - トークンベース認証
- **OAuth2** - ソーシャルログイン

### 統合サービス
- **Stripe** (24.16.0) - 決済処理
- **Spring Mail** - メール送信

### 開発ツール
- **Lombok** - ボイラープレートコード削減
- **MapStruct** - オブジェクトマッピング
- **SpringDoc OpenAPI** - API ドキュメント生成
- **Spring DevTools** - 開発効率化

### テスト
- **JUnit 5** - ユニットテスト
- **Spring Security Test** - セキュリティテスト
- **Testcontainers** - 統合テスト

## 前提条件

開発環境に以下がインストールされている必要があります：

- **Java 21** またはそれ以降
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Redis 7+**
- **Docker & Docker Compose**（オプション、推奨）

## セットアップ

### 1. リポジトリのクローン

```bash
git clone <repository-url>
cd ecommerce-backend
```

### 2. データベースのセットアップ

#### オプション A: Docker Compose を使用（推奨）

```bash
# プロジェクトルートディレクトリから実行
docker-compose up -d postgres redis
```

#### オプション B: ローカルインストール

PostgreSQL と Redis をローカルにインストールし、以下のデータベースを作成：

```sql
CREATE DATABASE ecommerce_db;
CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
```

### 3. 環境変数の設定

`src/main/resources/application.yml` を編集するか、環境変数を設定します：

```yaml
# データベース設定
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ecommerce_db
SPRING_DATASOURCE_USERNAME=ecommerce_user
SPRING_DATASOURCE_PASSWORD=ecommerce_password

# Redis設定
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# JWT設定
JWT_SECRET=your-256-bit-secret-key-for-jwt-tokens

# メール設定
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Stripe設定
STRIPE_PUBLIC_KEY=pk_test_your_stripe_public_key
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key

# OAuth2設定
GOOGLE_CLIENT_ID=your-google-oauth-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret
```

### 4. 依存関係のインストール

```bash
mvn clean install
```

## 環境変数

| 変数名 | 説明 | デフォルト値 |
|--------|------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL接続URL | `jdbc:postgresql://localhost:5432/ecommerce_db` |
| `SPRING_DATASOURCE_USERNAME` | データベースユーザー名 | `ecommerce_user` |
| `SPRING_DATASOURCE_PASSWORD` | データベースパスワード | `ecommerce_password` |
| `SPRING_DATA_REDIS_HOST` | Redisホスト | `localhost` |
| `SPRING_DATA_REDIS_PORT` | Redisポート | `6379` |
| `JWT_SECRET` | JWT署名用シークレットキー | - |
| `MAIL_USERNAME` | SMTPメールアドレス | - |
| `MAIL_PASSWORD` | SMTPパスワード | - |
| `STRIPE_PUBLIC_KEY` | Stripe公開鍵 | - |
| `STRIPE_SECRET_KEY` | Stripeシークレット鍵 | - |
| `GOOGLE_CLIENT_ID` | Google OAuth クライアントID | - |
| `GOOGLE_CLIENT_SECRET` | Google OAuth クライアントシークレット | - |

## 実行方法

### 開発モード

```bash
mvn spring-boot:run
```

### 本番ビルド

```bash
mvn clean package
java -jar target/ecommerce-backend-1.0.0.jar
```

### Docker を使用

```bash
# プロジェクトルートから
docker-compose up -d
```

アプリケーションは `http://localhost:8080` で起動します。

## API ドキュメント

アプリケーション起動後、以下のURLでSwagger UIにアクセスできます：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### 主要なエンドポイント

#### 認証
- `POST /api/auth/register` - ユーザー登録
- `POST /api/auth/login` - ログイン
- `POST /api/auth/refresh` - トークンリフレッシュ
- `POST /api/auth/logout` - ログアウト

#### 商品
- `GET /api/products` - 商品一覧取得
- `GET /api/products/{id}` - 商品詳細取得
- `POST /api/products` - 商品作成（管理者）
- `PUT /api/products/{id}` - 商品更新（管理者）
- `DELETE /api/products/{id}` - 商品削除（管理者）

#### カート
- `GET /api/cart` - カート取得
- `POST /api/cart/items` - カートに商品追加
- `PUT /api/cart/items/{id}` - カート商品更新
- `DELETE /api/cart/items/{id}` - カート商品削除

#### 注文
- `GET /api/orders` - 注文一覧取得
- `GET /api/orders/{id}` - 注文詳細取得
- `POST /api/orders` - 注文作成
- `PUT /api/orders/{id}/status` - 注文ステータス更新（管理者）

#### 決済
- `POST /api/payments/create-intent` - 決済インテント作成
- `POST /api/payments/webhook` - Stripe Webhook

## データベースマイグレーション

このプロジェクトは Flyway を使用してデータベースマイグレーションを管理しています。

### マイグレーションファイルの場所

```
src/main/resources/db/migration/
```

### マイグレーションの実行

アプリケーション起動時に自動的に実行されます。手動で実行する場合：

```bash
mvn flyway:migrate
```

### その他の Flyway コマンド

```bash
# マイグレーション情報の表示
mvn flyway:info

# マイグレーションの検証
mvn flyway:validate

# データベースのクリーン（開発環境のみ）
mvn flyway:clean
```

詳細は [FLYWAY_README.md](FLYWAY_README.md) を参照してください。

## プロジェクト構成

```
ecommerce-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ecommerce/ecommerce/
│   │   │       ├── ECommerceApp.java          # メインアプリケーションクラス
│   │   │       ├── api/                       # REST コントローラー
│   │   │       ├── config/                    # 設定クラス
│   │   │       ├── core/                      # コアビジネスロジック
│   │   │       │   ├── domain/               # ドメインモデル
│   │   │       │   ├── service/              # サービス層
│   │   │       │   └── repository/           # データアクセス層
│   │   │       ├── integration/              # 外部サービス統合
│   │   │       └── util/                     # ユーティリティクラス
│   │   └── resources/
│   │       ├── application.yml               # アプリケーション設定
│   │       └── db/migration/                 # Flyway マイグレーション
│   └── test/                                 # テストコード
├── target/                                   # ビルド出力
├── Dockerfile                                # Docker イメージ定義
├── pom.xml                                   # Maven 設定
└── README.md                                 # このファイル
```

## ヘルスチェック

アプリケーションのヘルスステータスを確認：

```bash
curl http://localhost:8080/api/actuator/health
```

## 監視

Spring Boot Actuator エンドポイント：

- **Health**: `/api/actuator/health`
- **Info**: `/api/actuator/info`
- **Metrics**: `/api/actuator/metrics`
- **Prometheus**: `/api/actuator/prometheus`

## セキュリティ

- すべてのパスワードは bcrypt でハッシュ化
- JWT トークンは有効期限付き（24時間）
- リフレッシュトークンは7日間有効
- CORS 設定でフロントエンドドメインを制限
- HTTPS 推奨（本番環境）

## トラブルシューティング

### データベース接続エラー

PostgreSQL が起動していることを確認：

```bash
docker-compose ps postgres
```

### Redis 接続エラー

Redis が起動していることを確認：

```bash
docker-compose ps redis
```

### ポート競合

デフォルトポート（8080）が使用中の場合、`application.yml` で変更：

```yaml
server:
  port: 8081
```

## ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。

## 貢献

プルリクエストを歓迎します。大きな変更の場合は、まず Issue を開いて変更内容を議論してください。

## サポート

問題が発生した場合は、GitHub Issues でお知らせください。

---

**開発者**: E-Commerce Team  
**バージョン**: 1.0.0  
**最終更新**: 2024
