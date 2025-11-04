package com.ecommerce.ecommerce.integration.service;

import com.ecommerce.ecommerce.core.domain.entity.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for payment integration with Stripe.
 * Handles payment processing, refunds, and payment method management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${app.stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Create payment intent for order
     */
    public PaymentIntent createPaymentIntent(Order order, String paymentMethodId) throws StripeException {
        log.info("Creating payment intent for order: {}", order.getOrderNumber());

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(order.getCurrency().toLowerCase())
                .putMetadata("orderId", order.getId().toString())
                .putMetadata("orderNumber", order.getOrderNumber())
                .putMetadata("userId", order.getUser().getId().toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                );

        // Attach payment method if provided
        if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
            paramsBuilder.setPaymentMethod(paymentMethodId);
            paramsBuilder.setConfirm(true);
        }

        PaymentIntentCreateParams params = paramsBuilder.build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        log.info("Payment intent created: {} for order: {}", paymentIntent.getId(), order.getOrderNumber());

        return paymentIntent;
    }

    /**
     * Confirm payment intent
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        log.info("Confirming payment intent: {}", paymentIntentId);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent confirmedPaymentIntent = paymentIntent.confirm();

        log.info("Payment intent confirmed: {} - Status: {}", paymentIntentId, confirmedPaymentIntent.getStatus());
        return confirmedPaymentIntent;
    }

    /**
     * Cancel payment intent
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        log.info("Cancelling payment intent: {}", paymentIntentId);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent cancelledPaymentIntent = paymentIntent.cancel();

        log.info("Payment intent cancelled: {}", paymentIntentId);
        return cancelledPaymentIntent;
    }

    /**
     * Create refund for payment
     */
    public com.stripe.model.Refund createRefund(String paymentIntentId, BigDecimal amount) throws StripeException {
        log.info("Creating refund for payment intent: {} - Amount: {}", paymentIntentId, amount);

        com.stripe.param.RefundCreateParams.Builder paramsBuilder = com.stripe.param.RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setMetadata(Map.of("refundReason", "customer_request"));

        // If partial refund, specify amount
        if (amount != null) {
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            paramsBuilder.setAmount(amountInCents);
        }

        com.stripe.param.RefundCreateParams params = paramsBuilder.build();
        com.stripe.model.Refund refund = com.stripe.model.Refund.create(params);

        log.info("Refund created: {} for payment intent: {}", refund.getId(), paymentIntentId);
        return refund;
    }

    /**
     * Attach payment method to customer
     */
    public PaymentMethod attachPaymentMethod(String paymentMethodId, String customerId) throws StripeException {
        log.info("Attaching payment method {} to customer {}", paymentMethodId, customerId);

        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethod attachedPaymentMethod = paymentMethod.attach(params);

        log.info("Payment method attached: {} to customer: {}", paymentMethodId, customerId);
        return attachedPaymentMethod;
    }

    /**
     * Detach payment method from customer
     */
    public PaymentMethod detachPaymentMethod(String paymentMethodId) throws StripeException {
        log.info("Detaching payment method: {}", paymentMethodId);

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethod detachedPaymentMethod = paymentMethod.detach();

        log.info("Payment method detached: {}", paymentMethodId);
        return detachedPaymentMethod;
    }

    /**
     * Create Stripe product
     */
    public Product createStripeProduct(String name, String description) throws StripeException {
        log.info("Creating Stripe product: {}", name);

        com.stripe.param.ProductCreateParams params = com.stripe.param.ProductCreateParams.builder()
                .setName(name)
                .setDescription(description)
                .setType(com.stripe.param.ProductCreateParams.Type.SERVICE)
                .build();

        Product product = Product.create(params);
        log.info("Stripe product created: {}", product.getId());

        return product;
    }

    /**
     * Create Stripe price
     */
    public Price createStripePrice(String productId, BigDecimal unitAmount, String currency) throws StripeException {
        log.info("Creating Stripe price for product: {} - Amount: {}", productId, unitAmount);

        long amountInCents = unitAmount.multiply(BigDecimal.valueOf(100)).longValue();

        com.stripe.param.PriceCreateParams params = com.stripe.param.PriceCreateParams.builder()
                .setProduct(productId)
                .setUnitAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .build();

        Price price = Price.create(params);
        log.info("Stripe price created: {}", price.getId());

        return price;
    }

    /**
     * Retrieve payment intent
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Process webhook event
     */
    public boolean processWebhook(String payload, String signature) {
        try {
            // Verify webhook signature
            Event event;
          event = Webhook.constructEvent(
                  payload, signature, webhookSecret);

          log.info("Processing webhook event: {} - Type: {}", event.getId(), event.getType());

            // Handle different event types
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                case "invoice.payment_succeeded":
                    handleInvoicePaymentSucceeded(event);
                    break;
                default:
                    log.info("Unhandled webhook event type: {}", event.getType());
            }

            return true;
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            return false;
        }
    }

    /**
     * Handle successful payment
     */
    private void handlePaymentSucceeded(com.stripe.model.Event event) {
        com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);

        if (paymentIntent != null) {
            String orderId = paymentIntent.getMetadata().get("orderId");
            log.info("Payment succeeded for order: {}", orderId);

            // Update order status in database
            // This would typically be handled by a separate service
        }
    }

    /**
     * Handle failed payment
     */
    private void handlePaymentFailed(com.stripe.model.Event event) {
        com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);

        if (paymentIntent != null) {
            String orderId = paymentIntent.getMetadata().get("orderId");
            log.info("Payment failed for order: {}", orderId);

            // Update order status in database
            // This would typically be handled by a separate service
        }
    }

    /**
     * Handle successful invoice payment
     */
    private void handleInvoicePaymentSucceeded(com.stripe.model.Event event) {
        com.stripe.model.Invoice invoice = (com.stripe.model.Invoice) event.getDataObjectDeserializer().getObject().orElse(null);

        if (invoice != null) {
            log.info("Invoice payment succeeded: {}", invoice.getId());
            // Handle subscription or recurring payment success
        }
    }

    /**
     * Get payment intent status
     */
    public String getPaymentIntentStatus(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.getStatus();
    }

    /**
     * Validate webhook signature
     */
    public boolean validateWebhookSignature(String payload, String signature) {
        try {
            Event event;
            event = Webhook.constructEvent(
                    payload, signature, webhookSecret);
            return true;
        } catch (Exception e) {
            log.error("Invalid webhook signature", e);
            return false;
        }
    }
}
