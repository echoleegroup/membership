package com.fih.aiovpoint.constant;

public class ApiKeyConstant {

    public enum ServiceKey {
        Account("TMiFpcL49i"),
        SocialMedia("3JmUBmeU2t"),
        ECSite("LnaitKyBxZ"),
        ;

        private String value;
        ServiceKey(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }
}
