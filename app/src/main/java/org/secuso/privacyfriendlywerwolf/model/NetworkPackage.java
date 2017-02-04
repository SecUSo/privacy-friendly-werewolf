package org.secuso.privacyfriendlywerwolf.model;

import org.secuso.privacyfriendlywerwolf.context.GameContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Every package send via network is encabsulated in this class to make sure, they can be handled appropriate
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class NetworkPackage<T> implements Serializable {

    public enum PACKAGE_TYPE { SERVER_HELLO, CLIENT_HELLO, START_GAME, UPDATE, VOTING_START, VOTING_RESULT, PHASE, DONE }

    private PACKAGE_TYPE messageType;
    private Map<String,String> options;
    private T payload;

    public NetworkPackage(PACKAGE_TYPE messageType) throws Exception {
        this.messageType = messageType;
        options = new HashMap();

    }

    public PACKAGE_TYPE getType() {
        return messageType;
    }

    public void setOption(String key, String value) {
        options.put(key, value);
    }

    public String getOption(String key) {
        return options.get(key);
    }

    /**
     * Class to set the payload for the network package.
     * It is important that the class makes sure, that only valid object types are set
     * @param object the object which should be set as the payload. the calling method has to chose the right object type
     * @throws Exception if the wrong classtype was chosen, throw an exception
     */
    public void setPayload(T object) throws Exception {

        switch (this.messageType) {
            case UPDATE:
            case START_GAME:
                if(!object.getClass().equals(GameContext.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
                break;
            case PHASE:
                if(!object.getClass().equals(GameContext.Phase.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
                break;
            case CLIENT_HELLO:
            case SERVER_HELLO:
                if(!object.getClass().equals(Player.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
                break;
            case VOTING_RESULT:
                if(!object.getClass().equals(String.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
                break;
            case DONE:
                if(!object.getClass().equals(GameContext.Phase.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
            default:
                break;
        }
    }

    /**
     * Class to return the set payload. When using this method you have to make sure to cast to the right object type
     * @return the casted object
     */
    public T getPayload() {

        return payload;

    }
}
