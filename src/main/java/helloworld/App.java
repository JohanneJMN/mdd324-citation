package helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, GatewayResponse> {

    public GatewayResponse handleRequest(final Object input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "https://pjvilloud.github.io");
        Genson genson = new GensonBuilder().useRuntimeType(true).create();
        try {
            final String pageContents = this.getPageContents("http://quotes.rest/qod.json");
            QuoteInput quoteInput = genson.deserialize(pageContents, QuoteInput.class);

            return new GatewayResponse(genson.serialize(quoteInput.getContents().getQuotes().get(0)), headers, 200);
        } catch (IOException e) {
            return new GatewayResponse("{error: "+e.getMessage()+"}", headers, 500);
        }
    }

    private String getPageContents(String address) throws IOException{
        BufferedReader br = null;
        StringJoiner lines = new StringJoiner(System.lineSeparator());
        try {
            URL url = new URL(address);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return lines.toString();
    }
}
