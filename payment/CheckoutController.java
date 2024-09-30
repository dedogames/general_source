package com.ideal.api.controllers;


import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CheckoutController {

    @Value("${stripe.key.secret}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) {
        List<Map<String, Object>> products = (List<Map<String, Object>>) data.get("products");

        if (products == null || products.isEmpty()) {
            return Map.of("error", "Products are required");
        }

        List<SessionCreateParams.LineItem> lineItems = products.stream().map(product -> {
            String dish = (String) product.get("dish");
            String imgdata = (String) product.get("imgdata");
            Integer price = (Integer) product.get("price");
            Integer qnty = (Integer) product.get("qnty");

            if (dish == null || imgdata == null || price == null || qnty == null) {
                throw new IllegalArgumentException("Product fields are required");
            }

            return SessionCreateParams.LineItem.builder()
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("brl")
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(dish)
                                    .addImage(imgdata)
                                    .build())
                            .setUnitAmount(price * 100L)
                            .build())
                    .setQuantity(qnty.longValue())
                    .build();
        }).collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
                .build();

        try {
            Session session = Session.create(params);
            return Map.of("id", session.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }
}
//package com.ideal.api.controllers;
//
//import com.stripe.Stripe;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//public class CheckoutController {
//
//    public CheckoutController() {
//        Stripe.apiKey = "sk_test_51PwP2V2Mq7bpa8c5c0tpWxGcKlMZWQaK1Jh3jgxrhAj6F3rcs6XtDWE0J4rMCVtH9GUsznnFtS9oeQMu0dWEGRHv00NDdWmGC2";
//    }
//
//    @PostMapping("/initiate-checkout")
//    public Map<String, String> initiateCheckout(@RequestBody Map<String, Object> data) {
//        List<Map<String, Object>> products = (List<Map<String, Object>>) data.get("products");
//        String sessionId = generateTemporarySessionId(products);
//        return Map.of("sessionId", sessionId);
//    }
//
//    @PostMapping("/finalize-checkout")
//    public Map<String, String> finalizeCheckout(@RequestBody Map<String, Object> data) {
//        String sessionId = (String) data.get("sessionId");
//        String paymentMethodTypeStr = (String) data.get("paymentMethodType");
//        SessionCreateParams.PaymentMethodType paymentMethodType = SessionCreateParams.PaymentMethodType.valueOf(paymentMethodTypeStr.toUpperCase());
//
//        List<Map<String, Object>> products = retrieveProductsFromSessionId(sessionId);
//
//        SessionCreateParams.LineItem[] lineItems = products.stream().map(product -> {
//            return SessionCreateParams.LineItem.builder()
//                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
//                            .setCurrency("brl")
//                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                    .setName((String) product.get("dish"))
//                                    .addImage((String) product.get("imgdata"))
//                                    .build())
//                            .setUnitAmount(((Integer) product.get("price")) * 100L)
//                            .build())
//                    .setQuantity(((Integer) product.get("qnty")).longValue())
//                    .build();
//        }).toArray(SessionCreateParams.LineItem[]::new);
//
//        SessionCreateParams params = SessionCreateParams.builder()
//                .addPaymentMethodType(paymentMethodType)
//                .addAllLineItem(Arrays.asList(lineItems))
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl("http://localhost:3000/success")
//                .setCancelUrl("http://localhost:3000/cancel")
//                .build();
//
//        try {
//            Session session = Session.create(params);
//            return Map.of("id", session.getId());
//        } catch (Exception e) {
//            return Map.of("error", e.getMessage());
//        }
//    }
//
//    private String generateTemporarySessionId(List<Map<String, Object>> products) {
//        // Implement your logic to generate and store a temporary session ID
//        return "temporarySessionId";
//    }
//
//    private List<Map<String, Object>> retrieveProductsFromSessionId(String sessionId) {
//        // Implement your logic to retrieve products using the session ID
//        return List.of();
//    }
//}
//
//
////package com.ideal.api.controllers;
////
////
////import com.stripe.Stripe;
////import com.stripe.model.checkout.Session;
////import com.stripe.param.checkout.SessionCreateParams;
////
////import org.springframework.web.bind.annotation.*;
////
////import java.util.Arrays;
////import java.util.List;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api")
////public class CheckoutController {
////
////    public CheckoutController() {
////
////        Stripe.apiKey =  "sk_test_51PwP2V2Mq7bpa8c5c0tpWxGcKlMZWQaK1Jh3jgxrhAj6F3rcs6XtDWE0J4rMCVtH9GUsznnFtS9oeQMu0dWEGRHv00NDdWmGC2";
////    }
////
////    @PostMapping("/create-checkout-session")
////    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) {
////        List<Map<String, Object>> products = (List<Map<String, Object>>) data.get("products");
////        String paymentMethodTypeStr = (String) data.get("paymentMethodType");
////        SessionCreateParams.PaymentMethodType paymentMethodType = SessionCreateParams.PaymentMethodType.valueOf(paymentMethodTypeStr.toUpperCase());
////
////        SessionCreateParams.LineItem[] lineItems = products.stream().map(product -> {
////            Map<String, Object> priceData = (Map<String, Object>) product.get("price_data");
////            Map<String, Object> productData = (Map<String, Object>) priceData.get("product_data");
////
////            return SessionCreateParams.LineItem.builder()
////                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
////                            .setCurrency((String) priceData.get("currency"))
////                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
////                                    .setName((String) productData.get("name"))
////                                    .addImage((String) productData.get("images"))
////                                    .build())
////                            .setUnitAmount((Long) priceData.get("unit_amount"))
////                            .build())
////                    .setQuantity((Long) product.get("quantity"))
////                    .build();
////        }).toArray(SessionCreateParams.LineItem[]::new);
////
////        SessionCreateParams params = SessionCreateParams.builder()
////                .addPaymentMethodType(paymentMethodType)
////                .addAllLineItem(Arrays.asList(lineItems))
////                .setMode(SessionCreateParams.Mode.PAYMENT)
////                .setSuccessUrl("http://localhost:3000/success")
////                .setCancelUrl("http://localhost:3000/cancel")
////                .build();
////
////        try {
////            Session session = Session.create(params);
////            return Map.of("id", session.getId());
////        } catch (Exception e) {
////            return Map.of("error", e.getMessage());
////        }
////    }
////}