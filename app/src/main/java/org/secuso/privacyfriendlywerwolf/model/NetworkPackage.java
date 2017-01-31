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

    public enum PACKAGE_TYPE { SERVER_HELLO, CLIENT_HELLO, START_GAME, UPDATE, VOTING_START, VOTING_RESULT, PHASE; }

    private PACKAGE_TYPE messageType;
    private Map<String,String> options;
    private T payload;

    public NetworkPackage(PACKAGE_TYPE messageType) throws Exception {
        this.messageType = messageType;
        options = new HashMap();

        /* switch (this.messageType) {
            case UPDATE:
            case START_GAME:
                if(!(payload instanceof GameContext)) throw new Exception("Wrong classstype for this method");
                break;
            case PHASE:
                if(!(payload instanceof Integer)) throw new Exception("Wrong classstype for this method");
            default:
                break;
        } */
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
        //TODO: this is redudant! remove this or remove the typechecking stuff
        //payload = object;

        switch (this.messageType) {
            case UPDATE:
            case START_GAME:
                if(!object.getClass().equals(GameContext.class)) throw new Exception("Wrong classstype for this method");
                payload = (T) object;
                break;
            case PHASE:
            case CLIENT_HELLO:
            case SERVER_HELLO:
                if(!object.getClass().equals(String.class)) throw new Exception("Wrong classstype for this method");
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

        /* switch (this.messageType) {
            case UPDATE:
                return payload;
            default:
                return null;
        } */
        return payload;

    }
}
