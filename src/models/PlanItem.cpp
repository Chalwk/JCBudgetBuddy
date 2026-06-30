#include "models/PlanItem.h"
#include "utils/FinanceUtils.h"

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

QJsonObject PlanItem::toJson() const {
    QJsonObject obj;
    obj["name"] = name;
    obj["description"] = description;
    obj["totalCost"] = totalCost;
    obj["deposit"] = deposit;
    obj["weeklyPayment"] = weeklyPayment;
    return obj;
}

PlanItem PlanItem::fromJson(const QJsonObject& obj) {
    PlanItem item;
    item.name = obj.value("name").toString();
    item.description = obj.value("description").toString();
    item.totalCost = obj.value("totalCost").toDouble(0.0);
    item.deposit = obj.value("deposit").toDouble(0.0);
    item.weeklyPayment = obj.value("weeklyPayment").toDouble(0.0);
    return item;
}
