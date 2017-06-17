package io.crossbar.autobahn.wamp.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.crossbar.autobahn.wamp.exceptions.ProtocolError;
import io.crossbar.autobahn.wamp.interfaces.IMessage;
import io.crossbar.autobahn.wamp.utils.Cast;

public class Unsubscribed implements IMessage {

    public static final int MESSAGE_TYPE = 35;

    private static final long SUBSCRIPTION_NULL = -1;
    private final long request;
    private final long subscription;
    private final String reason;

    public Unsubscribed(long request, long subscription, String reason) {
        this.request = request;
        this.subscription = subscription;
        this.reason = reason;
    }

    public static Unsubscribed parse(List<Object> wmsg) {
        if (wmsg.size() == 0 || !(wmsg.get(0) instanceof Integer) || (int) wmsg.get(0) != MESSAGE_TYPE) {
            throw new IllegalArgumentException("Invalid message.");
        }

        if (wmsg.size() < 2 || wmsg.size() > 3) {
            throw new ProtocolError(String.format("invalid message length %s for UNSUBSCRIBED", wmsg.size()));
        }

        long request = Cast.castRequestID(wmsg.get(1));

        long subscription = SUBSCRIPTION_NULL;
        String reason = null;
        if (wmsg.size() > 2) {
            Map<String, Object> details = (Map<String, Object>) wmsg.get(2);
            if (details.containsKey("subscription")) {
                subscription = (long) details.get("subscription");
            }
            if (details.containsKey("reason")) {
                reason = (String) details.get("reason");
            }
        }

        return new Unsubscribed(request, subscription, reason);
    }

    @Override
    public List<Object> marshal() {
        List<Object> marshaled = new ArrayList<>();
        marshaled.add(MESSAGE_TYPE);
        marshaled.add(request);
        if (subscription != SUBSCRIPTION_NULL || reason != null) {
            Map<String, Object> details = new HashMap<>();
            if (reason != null) {
                details.put("reason", reason);
            }
            if (subscription != SUBSCRIPTION_NULL) {
                details.put("subscription", subscription);
            }
            marshaled.add(details);
        }
        return marshaled;
    }
}
