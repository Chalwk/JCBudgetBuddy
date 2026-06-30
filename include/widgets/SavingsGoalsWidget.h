#pragma once

#include <QWidget>
#include <QVBoxLayout>
#include <QScrollArea>

class SavingsGoalsWidget : public QWidget {
    Q_OBJECT

public:
    explicit SavingsGoalsWidget(QWidget* parent = nullptr);

public slots:
    void refresh();

private slots:
    void logPaymentForPlan(int planIndex);
    void managePaymentsForPlan(int planIndex);

private:
    QScrollArea* m_scrollArea{};
    QWidget* m_container{};
    QVBoxLayout* m_containerLayout{};

    void rebuildUI();
};