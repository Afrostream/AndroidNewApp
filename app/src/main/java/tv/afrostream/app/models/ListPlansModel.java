package tv.afrostream.app.models;

/**
 * Created by bahri on 31/01/2017.
 */

public class ListPlansModel {

    String internalPlanUuid;
    String amountInCents;

    String name;
    String description;
    String currency;
    String periodUnit;
    String periodLength;
    Boolean Showlogo;

    String providerPlanUuid;

    String providerName;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }


    public String getProviderPlanUuid() {
        return providerPlanUuid;
    }

    public void setProviderPlanUuid(String providerPlanUuid) {
        this.providerPlanUuid = providerPlanUuid;
    }

    public Boolean getCouponCodeCompatible() {
        return isCouponCodeCompatible;
    }

    public void setCouponCodeCompatible(Boolean couponCodeCompatible) {
        isCouponCodeCompatible = couponCodeCompatible;
    }

    public Boolean getShowlogo() {
        return Showlogo;
    }

    public void setShowlogo(Boolean Showlogo) {
        Showlogo = Showlogo;
    }

    Boolean isCouponCodeCompatible;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    String amount;
    public ListPlansModel() {
    }

    public ListPlansModel(String internalPlanUuid,String amount, String amountInCents, String name, String description, String currency, String periodUnit, String periodLength,Boolean isCouponCodeCompatible,Boolean Showlogo,String providerPlanUuid,String providerName) {
        this.internalPlanUuid = internalPlanUuid;
        this.amountInCents = amountInCents;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.periodUnit = periodUnit;
        this.periodLength = periodLength;
        this.amount=amount;
        this.isCouponCodeCompatible=isCouponCodeCompatible;
        this.Showlogo=Showlogo;
        this.providerPlanUuid=providerPlanUuid;
        this.providerName=providerName;

    }

    public String getInternalPlanUuid() {
        return internalPlanUuid;
    }

    public void setInternalPlanUuid(String internalPlanUuid) {
        this.internalPlanUuid = internalPlanUuid;
    }

    public String getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(String amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(String periodUnit) {
        this.periodUnit = periodUnit;
    }

    public String getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(String periodLength) {
        this.periodLength = periodLength;
    }
}
