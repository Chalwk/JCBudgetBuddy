#include "models/Payment.h"

QJsonObject Payment::toJson() const {
    QJsonObject obj;
    obj["date"] = date.isValid() ? date.toString(Qt::ISODate) : QString();
    obj["amount"] = amount;
    return obj;
}

Payment Payment::fromJson(const QJsonObject& obj) {
    Payment p;
    p.date = QDate::fromString(obj.value("date").toString(), Qt::ISODate);
    p.amount = obj.value("amount").toDouble(0.0);
    return p;
}
