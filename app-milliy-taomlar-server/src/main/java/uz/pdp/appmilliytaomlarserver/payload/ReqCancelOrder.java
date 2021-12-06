package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.List;

@Data
public class ReqCancelOrder {
    private boolean fullCancel;
    private List<ReqProductWithAmount> reqProductWithAmountList;
}
