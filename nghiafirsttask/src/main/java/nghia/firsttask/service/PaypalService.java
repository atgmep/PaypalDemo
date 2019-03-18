package nghia.firsttask.service;

import java.util.*;

import com.paypal.api.payments.*;
import nghia.firsttask.common.ConstantList;
import nghia.firsttask.config.PaypalPaymentIntent;
import nghia.firsttask.config.PaypalPaymentMethod;
import nghia.firsttask.entity.Product;
import nghia.firsttask.repository.ProductRepository;
import org.springframework.stereotype.Service;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import javax.servlet.http.HttpServletRequest;

@Service
public class PaypalService {


    private APIContext apiContext;
    private ProductRepository productRepository;

    public PaypalService(APIContext apiContext, ProductRepository productRepository) {
        this.apiContext = apiContext;
        this.productRepository = productRepository;
    }

    private Payment createPayment(double price, double taxRate, String currency, PaypalPaymentMethod method, PaypalPaymentIntent intent,
                                  String description, String cancelUrl, String successUrl) {

        int cost = (int) (price * 100);
        int tax = (int) (cost * taxRate);
        double total = cost + tax;
        String costStr = String.format("%.2f", price);
        String taxStr = String.format("%.2f", ((double) tax) / 100);
        String totalStr = String.format("%.2f", total / 100);

//        Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

//        Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

//        Set payment details
        Details details = new Details();
        details.setShipping("0");
        details.setSubtotal(costStr);
        details.setTax(taxStr);
//        Payment amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
//        Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal(totalStr);
        amount.setDetails(details);

// Transaction information
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);

// Add transaction to a list
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

// Add payment details
        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);

        return payment;
    }

    public String initPayment(int proId, String cancelUrl, String successUrl) {
        try {
            Optional<Product> optional = productRepository.findById(proId);
            if (optional.isPresent()) {
                Product productEntity = optional.get();
                String name = productEntity.getName();
                double price = productEntity.getPrice();

                Payment payment = createPayment(price, ConstantList.DEF_TAX_RATE, ConstantList.DEF_CURRENCY,
                        PaypalPaymentMethod.paypal, PaypalPaymentIntent.sale, "Purchase " + name,
                        ConstantList.LOCAL_URL + cancelUrl, ConstantList.LOCAL_URL + successUrl);

                Payment createdPayment = payment.create(apiContext);
                for (Links links : createdPayment.getLinks()) {
                    if (links.getRel().equalsIgnoreCase("approval_url")) {
                        return "redirect:" + links.getHref();
                    }
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }


//    public boolean executePayment(Payment payment) {
//        try {
//            Payment createdPayment = payment.create(apiContext);
//            for (Links link : createdPayment.getLinks()) {
//                if (link.getRel().equalsIgnoreCase("approval_url")) {
//                    // Redirect the customer to link.getHref()
//                }
//            }
//            return true;
//        } catch (PayPalRESTException e) {
//            System.err.println(e.getDetails());
//            return false;
//        }
//    }


//
//    private Map<String, String> map = new HashMap<>();
//
//    public Payment createPayment(HttpServletRequest req) {
//        Payment createdPayment = null;
//
//        // ### Api Context
//        // Pass in a `ApiContext` object to authenticate
//        // the call and to send a unique request id
//        // (that ensures idempotency). The SDK generates
//        // a request id if you do not pass one explicitly.
//        APIContext apiContext = new APIContext(ConstantList.PAYPAL_CLIENT_ID,
//                ConstantList.PAYPAL_CLIENT_SECRET, ConstantList.PAYPAL_MODE);
//        if (req.getParameter("PayerID") != null) {
//            Payment payment = new Payment();
//            if (req.getParameter("guid") != null) {
//                payment.setId(map.get(req.getParameter("guid")));
//            }
//
//            PaymentExecution paymentExecution = new PaymentExecution();
//            paymentExecution.setPayerId(req.getParameter("PayerID"));
//            try {
//
//                createdPayment = payment.execute(apiContext, paymentExecution);
//                System.out.println("Executed The Payment");
//            } catch (PayPalRESTException e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            // ###Details
//            // Let's you specify details of a payment amount.
//            Details details = new Details();
//            details.setShipping("1");
//            details.setSubtotal("5");
//            details.setTax("1");
//
//            // ###Amount
//            // Let's you specify a payment amount.
//            Amount amount = new Amount();
//            amount.setCurrency("USD");
//            // Total must be equal to sum of shipping, tax and subtotal.
//            amount.setTotal("7");
//            amount.setDetails(details);
//
//            // ###Transaction
//            // A transaction defines the contract of a
//            // payment - what is the payment for and who
//            // is fulfilling it. Transaction is created with
//            // a `Payee` and `Amount` types
//            Transaction transaction = new Transaction();
//            transaction.setAmount(amount);
//            transaction
//                    .setDescription("This is the payment transaction description.");
//
//            // ### Items
//            Item item = new Item();
//            item.setName("Ground Coffee 40 oz").setQuantity("1").setCurrency("USD").setPrice("5");
//            ItemList itemList = new ItemList();
//            List<Item> items = new ArrayList<>();
//            items.add(item);
//            itemList.setItems(items);
//
//            transaction.setItemList(itemList);
//
//
//            // The Payment creation API requires a list of
//            // Transaction; add the created `Transaction`
//            // to a List
//            List<Transaction> transactions = new ArrayList<>();
//            transactions.add(transaction);
//
//            // ###Payer
//            // A resource representing a Payer that funds a payment
//            // Payment Method
//            // as 'paypal'
//            Payer payer = new Payer();
//            payer.setPaymentMethod("paypal");
//
//            // ###Payment
//            // A Payment Resource; create one using
//            // the above types and intent as 'sale'
//            Payment payment = new Payment();
//            payment.setIntent("sale");
//            payment.setPayer(payer);
//            payment.setTransactions(transactions);
//
//            // ###Redirect URLs
//            RedirectUrls redirectUrls = new RedirectUrls();
//            String guid = UUID.randomUUID().toString().replaceAll("-", "");
//            redirectUrls.setCancelUrl(req.getScheme() + "://"
//                    + req.getServerName() + ":" + req.getServerPort()
//                    + req.getContextPath() + "/paymentwithpaypal?guid=" + guid);
//            redirectUrls.setReturnUrl(req.getScheme() + "://"
//                    + req.getServerName() + ":" + req.getServerPort()
//                    + req.getContextPath() + "/paymentwithpaypal?guid=" + guid);
//            payment.setRedirectUrls(redirectUrls);
//
//            // Create a payment by posting to the APIService
//            // using a valid AccessToken
//            // The return object contains the status;
//            try {
//                createdPayment = payment.create(apiContext);
//                System.out.println("Created payment with id = " + createdPayment.getId() + " and status = " + createdPayment.getState());
//                // ###Payment Approval Url
//                for (Links link : createdPayment.getLinks()) {
//                    if (link.getRel().equalsIgnoreCase("approval_url")) {
//                        req.setAttribute("redirectURL", link.getHref());
//                    }
//                }
//                System.out.println("Payment with PayPal " + Payment.getLastRequest() + Payment.getLastResponse());
//                map.put(guid, createdPayment.getId());
//            } catch (PayPalRESTException e) {
//                e.printStackTrace();
//            }
//        }
//        return createdPayment;
//    }

//    public Payment createPaymentOld(
//            Double total,
//            String currency,
//            PaypalPaymentMethod method,
//            PaypalPaymentIntent intent,
//            String description,
//            String cancelUrl,
//            String successUrl) throws PayPalRESTException {
//
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format("%.2f", total));
//
//        Transaction transaction = new Transaction();
//        transaction.setDescription(description);
//        transaction.setAmount(amount);
//
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod(method.toString());
//
//        Payment payment = new Payment();
//        payment.setIntent(intent.toString());
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl(cancelUrl);
//        redirectUrls.setReturnUrl(successUrl);
//        payment.setRedirectUrls(redirectUrls);
//        apiContext.setMaskRequestId(true);
//        return payment.create(apiContext);
//    }


}