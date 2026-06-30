// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "models/PlanItem.h"
#include "utils/FinanceUtils.h"
#include <QJsonArray>
#include <QtMath>

double PlanItem::remainingCost() const {
    return qMax(0.0, totalCost - deposit);
}

double PlanItem::weeksToComplete() const {
    if (weeklyPayment <= 0.0) return 0.0;
    return remainingCost() / weeklyPayment;
}

QString PlanItem::timeframeString() const {
    return formatTimeframe(weeksToComplete());
}

double PlanItem::totalPaid() const {
    double sum = 0.0;
    for (const Payment& p : payments)
        sum += p.amount;
    return sum;
}

double PlanItem::remainingAfterPayments() const {
    return qMax(0.0, totalCost - totalPaid());
}

double PlanItem::progressPercentage() const {
    if (totalCost <= 0.0) return 0.0;
    return (totalPaid() / totalCost) * 100.0;
}

QDate PlanItem::estimatedCompletionDate() const {
    if (weeklyPayment <= 0.0) return QDate();
    double remaining = remainingAfterPayments();
    double weeks = remaining / weeklyPayment;
    return QDate::currentDate().addDays(static_cast<int>(qCeil(weeks * 7)));
}

void PlanItem::addPayment(const Payment& payment) {
    payments.append(payment);
}

QJsonObject PlanItem::toJson() const {
    QJsonObject obj;
    obj["name"] = name;
    obj["description"] = description;
    obj["totalCost"] = totalCost;
    obj["deposit"] = deposit;
    obj["weeklyPayment"] = weeklyPayment;

    QJsonArray paymentsArray;
    for (const Payment& p : payments) {
        paymentsArray.append(p.toJson());
    }
    obj["payments"] = paymentsArray;

    return obj;
}

PlanItem PlanItem::fromJson(const QJsonObject& obj) {
    PlanItem item;
    item.name = obj.value("name").toString();
    item.description = obj.value("description").toString();
    item.totalCost = obj.value("totalCost").toDouble(0.0);
    item.deposit = obj.value("deposit").toDouble(0.0);
    item.weeklyPayment = obj.value("weeklyPayment").toDouble(0.0);

    const QJsonArray paymentsArray = obj.value("payments").toArray();
    for (const QJsonValue& val : paymentsArray) {
        if (val.isObject())
            item.payments.append(Payment::fromJson(val.toObject()));
    }

    return item;
}