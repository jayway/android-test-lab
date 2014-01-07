
package lab.jayway.logging;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger logger = new Logger();

        String rootUrl = "url";
        logger.init(this, rootUrl);

        logger.log("type").with("key", "value").submit();

    }

}
