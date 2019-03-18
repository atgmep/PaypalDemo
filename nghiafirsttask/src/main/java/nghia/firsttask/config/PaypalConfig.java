package nghia.firsttask.config;

import java.util.HashMap;
import java.util.Map;

import nghia.firsttask.common.ConstantList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;

@Configuration
public class PaypalConfig {

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> sdkConfig = new HashMap<>();
        sdkConfig.put("mode", ConstantList.PAYPAL_MODE);
        return sdkConfig;
    }

    @Bean
    public OAuthTokenCredential authTokenCredential() {
        return new OAuthTokenCredential(ConstantList.PAYPAL_CLIENT_ID, ConstantList.PAYPAL_CLIENT_SECRET, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() {
//        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
//        apiContext.setConfigurationMap(paypalSdkConfig());
        return new APIContext(ConstantList.PAYPAL_CLIENT_ID, ConstantList.PAYPAL_CLIENT_SECRET, ConstantList.PAYPAL_MODE);
    }
}