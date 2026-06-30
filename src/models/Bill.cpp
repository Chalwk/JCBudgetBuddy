#include "models/Bill.h"

double Bill::weeklyAmount() const {
    return weeklyExpenseContribution(frequency, paymentMethod, amount);
}

QJsonObject Bill::toJson() const {
    QJsonObject obj;
    obj["name"] = name;
    obj["amount"] = amount;
    obj["frequency"] = billFrequencyToString(frequency);
    obj["paymentDay"] = paymentDay;
    obj["paymentMethod"] = paymentMethodToString(paymentMethod);
    obj["notes"] = notes;
    return obj;
}

Bill Bill::fromJson(const QJsonObject& obj) {
    Bill bill;
    bill.name = obj.value("name").toString();
    bill.amount = obj.value("amount").toDouble(0.0);
    bill.frequency = billFrequencyFromString(obj.value("frequency").toString());
    bill.paymentDay = obj.value("paymentDay").toString();
    bill.paymentMethod = paymentMethodFromString(obj.value("paymentMethod").toString());
    bill.notes = obj.value("notes").toString();
    return bill;
}
