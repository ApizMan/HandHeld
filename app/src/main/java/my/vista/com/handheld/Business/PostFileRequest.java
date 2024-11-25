package my.vista.com.handheld.Business;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by hp on 26/8/2016.
 */
public class PostFileRequest extends StringRequest {

    private final InputStream mStream;

    public PostFileRequest(final String url,
                           final Response.Listener<String> listener,
                           final Response.ErrorListener errorListener,
                           final InputStream stream) {
        super(Method.POST, url, listener, errorListener);
        mStream = stream;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        streamToStream(mStream, out);
        return out.toByteArray();
    }

    public static void streamToStream(final InputStream in, final OutputStream out) {
        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
