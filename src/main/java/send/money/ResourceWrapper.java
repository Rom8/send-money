package send.money;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateos.AbstractResource;

@Produces(MediaType.APPLICATION_HAL_JSON)
public class ResourceWrapper extends AbstractResource<ResourceWrapper> {

    @JsonUnwrapped
    private Object element;

    public ResourceWrapper() {
    }

    public ResourceWrapper(Object element) {
        this.element = element;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

}
