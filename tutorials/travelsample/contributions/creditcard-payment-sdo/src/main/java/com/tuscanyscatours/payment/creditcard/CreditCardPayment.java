package com.tuscanyscatours.payment.creditcard;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "CreditCardPayment", targetNamespace = "http://tuscanyscatours.com/CreditCardPayment/")
public interface CreditCardPayment {

    /**
     * 
     * @param amount
     * @param creditCard
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "http://tuscanyscatours.com/CreditCardPayment/authorize")
    @WebResult(name = "Status", targetNamespace = "")
    @RequestWrapper(localName = "authorize", targetNamespace = "http://tuscanyscatours.com/CreditCardPayment/", className = "com.tuscanyscatours.payment.creditcard.AuthorizeType")
    @ResponseWrapper(localName = "authorizeResponse", targetNamespace = "http://tuscanyscatours.com/CreditCardPayment/", className = "com.tuscanyscatours.payment.creditcard.AuthorizeResponseType")
    public String authorize(@WebParam(name = "CreditCard", targetNamespace = "") CreditCardDetailsType creditCard,
                            @WebParam(name = "Amount", targetNamespace = "") float amount);

}
