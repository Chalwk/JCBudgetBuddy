// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "models/UserData.h"
#include <QJsonArray>

static QJsonArray serializeIncomes(const QList<IncomeStream>& list) {
    QJsonArray arr;
    for (const auto& item : list) {
        arr.append(item.toJson());
    }
    return arr;
}

static QJsonArray serializeBills(const QList<Bill>& list) {
    QJsonArray arr;
    for (const auto& item : list) {
        arr.append(item.toJson());
    }
    return arr;
}

static QJsonArray serializeInvoices(const QList<Invoice>& list) {
    QJsonArray arr;
    for (const auto& item : list) {
        arr.append(item.toJson());
    }
    return arr;
}

static QJsonArray serializePlans(const QList<PlanItem>& list) {
    QJsonArray arr;
    for (const auto& item : list) {
        arr.append(item.toJson());
    }
    return arr;
}

QJsonObject UserData::toJson() const {
    QJsonObject obj;
    obj["incomes"] = serializeIncomes(incomes);
    obj["weeklyBills"] = serializeBills(weeklyBills);
    obj["monthlyBills"] = serializeBills(monthlyBills);
    obj["invoices"] = serializeInvoices(invoices);
    obj["plans"] = serializePlans(plans);
    return obj;
}

static void deserializeIncomes(const QJsonArray& arr, QList<IncomeStream>& list) {
    list.clear();
    for (const auto& value : arr) {
        if (value.isObject()) list.append(IncomeStream::fromJson(value.toObject()));
    }
}

static void deserializeBills(const QJsonArray& arr, QList<Bill>& list) {
    list.clear();
    for (const auto& value : arr) {
        if (value.isObject()) list.append(Bill::fromJson(value.toObject()));
    }
}

static void deserializeInvoices(const QJsonArray& arr, QList<Invoice>& list) {
    list.clear();
    for (const auto& value : arr) {
        if (value.isObject()) list.append(Invoice::fromJson(value.toObject()));
    }
}

static void deserializePlans(const QJsonArray& arr, QList<PlanItem>& list) {
    list.clear();
    for (const auto& value : arr) {
        if (value.isObject()) list.append(PlanItem::fromJson(value.toObject()));
    }
}

UserData UserData::fromJson(const QJsonObject& obj) {
    UserData data;
    deserializeIncomes(obj.value("incomes").toArray(), data.incomes);
    deserializeBills(obj.value("weeklyBills").toArray(), data.weeklyBills);
    deserializeBills(obj.value("monthlyBills").toArray(), data.monthlyBills);
    deserializeInvoices(obj.value("invoices").toArray(), data.invoices);
    deserializePlans(obj.value("plans").toArray(), data.plans);
    return data;
}
