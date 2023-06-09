package openerp.containertransport.constants;

import lombok.Getter;

import java.io.Serializable;

public class Constants implements Serializable {
    @Getter
    public enum ActionType {
        MONTHLY(1, "MONTHLY"),
        WEEKLY(2, "WEEKLY"),
        DAILY(3, "DAILY");
        private final Integer code;
        private final String value;

        ActionType(Integer code, String value) {
            this.code = code;
            this.value = value;
        }
    }

    @Getter
    public enum TruckStatus {

        AVAILABLE("AVAILABLE"),
        SCHEDULED("SCHEDULED"),
        UNAVAILABLE("UNAVAILABLE"),
        EXECUTING("EXECUTING");
        private final String status;
        TruckStatus(String status) {
            this.status = status;
        }
    }

    @Getter
    public enum TrailerStatus {
        AVAILABLE("AVAILABLE"),
        SCHEDULED("SCHEDULED"),
        UNAVAILABLE("UNAVAILABLE"),
        EXECUTING("EXECUTING");
        private final String status;
        TrailerStatus(String status) {
            this.status = status;
        }
    }

    @Getter
    public enum OrderStatus {
        ORDERED("ORDERED"),
        SCHEDULED("SCHEDULED"),
        DONE("DONE"),
        EXECUTING("EXECUTING");
        private final String status;
        OrderStatus(String status) {
            this.status = status;
        }
    }

    @Getter
    public enum ContainerStatus {
        ORDERED("ORDERED"),
        SCHEDULED("SCHEDULED"),
        AVAILABLE("AVAILABLE"),
        EXECUTING("EXECUTING");
        private final String status;
        ContainerStatus(String status) {
            this.status = status;
        }
    }
}
