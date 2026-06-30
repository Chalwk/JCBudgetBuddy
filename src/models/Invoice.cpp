// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "models/Invoice.h"
#include <QJsonArray>

double Invoice::balance() const {
    double paid = 0.0;
    for (const auto& payment : payments) {
        paid += payment.amount;
    }
    return total - paid;
}

QJsonObject Invoice::toJson() const {
    QJsonObject obj;
    obj["number"] = number;
    obj["total"] = total;
    QJsonArray arr;
    for (const auto& payment : payments) {
        arr.append(payment.toJson());
    }
    obj["payments"] = arr;
    return obj;
}

Invoice Invoice::fromJson(const QJsonObject& obj) {
    Invoice invoice;
    invoice.number = obj.value("number").toString();
    invoice.total = obj.value("total").toDouble(0.0);
    const auto paymentsValue = obj.value("payments").toArray();
    for (const auto& value : paymentsValue) {
        if (value.isObject()) {
            invoice.payments.append(Payment::fromJson(value.toObject()));
        }
    }
    return invoice;
}
