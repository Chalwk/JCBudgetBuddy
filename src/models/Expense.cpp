// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "models/Expense.h"

QJsonObject Expense::toJson() const
{
    QJsonObject obj;
    obj["date"] = date.isValid() ? date.toString(Qt::ISODate) : QString();
    obj["description"] = description;
    obj["amount"] = amount;
    obj["category"] = category;
    return obj;
}

Expense Expense::fromJson(const QJsonObject &obj)
{
    Expense e;
    e.date = QDate::fromString(obj.value("date").toString(), Qt::ISODate);
    e.description = obj.value("description").toString();
    e.amount = obj.value("amount").toDouble(0.0);
    e.category = obj.value("category").toString();
    return e;
}