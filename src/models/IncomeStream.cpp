// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "models/IncomeStream.h"

bool IncomeStream::isCurrentlyActive() const {
    if (frequency == IncomeFrequency::OneOff) {
        return active && amount > 0.0;
    }
    if (!active) return false;
    if (startDate.isValid() && QDate::currentDate() < startDate) return false;
    if (endDate && endDate->isValid() && QDate::currentDate() > *endDate) return false;
    return true;
}

double IncomeStream::weeklyAmount() const {
    return isCurrentlyActive() ? weeksFromFrequency(frequency, amount) : 0.0;
}

QJsonObject IncomeStream::toJson() const {
    QJsonObject obj;
    obj["name"] = name;
    obj["amount"] = amount;
    obj["frequency"] = incomeFrequencyToString(frequency);
    obj["startDate"] = startDate.isValid() ? startDate.toString(Qt::ISODate) : QString();
    if (endDate && endDate->isValid()) {
        obj["endDate"] = endDate->toString(Qt::ISODate);
    }
    obj["active"] = active;
    obj["notes"] = notes;
    return obj;
}

IncomeStream IncomeStream::fromJson(const QJsonObject& obj) {
    IncomeStream stream;
    stream.name = obj.value("name").toString();
    stream.amount = obj.value("amount").toDouble(0.0);
    stream.frequency = incomeFrequencyFromString(obj.value("frequency").toString());
    stream.startDate = QDate::fromString(obj.value("startDate").toString(), Qt::ISODate);
    stream.endDate = parseOptionalDate(obj, "endDate");
    stream.active = obj.value("active").toBool(true);
    stream.notes = obj.value("notes").toString();
    return stream;
}
