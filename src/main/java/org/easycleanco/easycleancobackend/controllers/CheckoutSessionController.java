package org.easycleanco.easycleancobackend.controllers;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.easycleanco.easycleancobackend.models.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/checkout-session")
public class CheckoutSessionController {
    @PostMapping()
    public ResponseEntity<String> createCheckoutSession(@RequestBody List<Booking> bookings) {
        try {
            Stripe.apiKey = "sk_test_51QqQTPGdRApPkwvIUWytpIDDmJEXT5laYP64ViHBul4HIl4a9SVExojR26pzor4Gieg4ki78uXmfWTKEJK0DW37v00uBAW8ftL";

            // To build line items from the bookings
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

            // Loop through the bookings and create line items
            for (Booking booking : bookings) {
                //Product Data
                SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(booking.getServiceName())
                    .build();


                //Price Data
                SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("sgd")
                    .setUnitAmount((long) (booking.getHourlyRate() * booking.getDuration() * 100)) // Convert to cents
                    .setProductData(productData)
                    .build();


                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(priceData)
                    .setQuantity(1L)
                        .addTaxRate("txr_1QqVnoGdRApPkwvIsVoeDC4W")
                    .build();

                lineItems.add(lineItem);
            }

            // Create a new Checkout Session
            String host = "http://localhost:8080/EasyCleanCo";

            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(host + "/create-booking")
                .setCancelUrl(host + "/cart.jsp?err=cancelled")
                .addAllLineItem(lineItems)
                .build();
            Session session = Session.create(params);

            return ResponseEntity.ok(session.getId());
        } catch (StripeException e) {
            // Log the exception and return an error response
            return ResponseEntity.status(500).body("Stripe error: " + e.getMessage());
        }
    }
}
