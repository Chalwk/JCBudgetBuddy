#pragma once

#include <QDialog>
#include <QLineEdit>
#include <QDoubleSpinBox>
#include <QComboBox>
#include <QDateEdit>
#include <QCheckBox>
#include <QTextEdit>
#include <QTableView>
#include <QStandardItemModel>
#include <QLabel>
#include <QPushButton>
#include <optional>

#include "models/IncomeStream.h"
#include "models/Bill.h"
#include "models/Invoice.h"
#include "models/PlanItem.h"
#include "data/DataManager.h"

class IncomeDialog : public QDialog {
    Q_OBJECT
public:
    explicit IncomeDialog(QWidget* parent = nullptr, const IncomeStream* existing = nullptr);

    IncomeStream resultData() const;

protected slots:
    void accept() override;

private:
    QLineEdit* m_nameEdit{};
    QDoubleSpinBox* m_amountSpin{};
    QComboBox* m_frequencyCombo{};
    QDateEdit* m_startDateEdit{};
    QCheckBox* m_useEndDateCheck{};
    QDateEdit* m_endDateEdit{};
    QCheckBox* m_activeCheck{};
    QTextEdit* m_notesEdit{};
};

class BillDialog : public QDialog {
    Q_OBJECT
public:
    explicit BillDialog(bool monthlyMode, QWidget* parent = nullptr, const Bill* existing = nullptr);

    Bill resultData() const;

protected slots:
    void accept() override;

private:
    bool m_monthlyMode = false;
    QLineEdit* m_nameEdit{};
    QDoubleSpinBox* m_amountSpin{};
    QComboBox* m_frequencyCombo{};
    QLineEdit* m_paymentDayEdit{};
    QComboBox* m_paymentMethodCombo{};
    QTextEdit* m_notesEdit{};
};

class InvoiceDialog : public QDialog {
    Q_OBJECT
public:
    explicit InvoiceDialog(QWidget* parent = nullptr, const Invoice* existing = nullptr);

    Invoice resultData() const;

protected slots:
    void accept() override;

private:
    QLineEdit* m_numberEdit{};
    QDoubleSpinBox* m_totalSpin{};
};

class PaymentDialog : public QDialog {
    Q_OBJECT
public:
    explicit PaymentDialog(QWidget* parent = nullptr, const Payment* existing = nullptr);

    Payment resultData() const;

protected slots:
    void accept() override;

private:
    QDateEdit* m_dateEdit{};
    QDoubleSpinBox* m_amountSpin{};
};

class PlanDialog : public QDialog {
    Q_OBJECT
public:
    explicit PlanDialog(QWidget* parent = nullptr, const PlanItem* existing = nullptr);

    PlanItem resultData() const;

protected slots:
    void accept() override;

private:
    QLineEdit* m_nameEdit{};
    QTextEdit* m_descEdit{};
    QDoubleSpinBox* m_totalSpin{};
    QDoubleSpinBox* m_depositSpin{};
    QDoubleSpinBox* m_weeklyPaymentSpin{};
};

class PaymentsDialog : public QDialog {
    Q_OBJECT
public:
    explicit PaymentsDialog(Invoice* invoice, QWidget* parent = nullptr);

private slots:
    void addPayment();
    void editPayment();
    void deletePayment();
    void onDoubleClicked(const QModelIndex& index);
    void refresh();
    void accept() override;

private:
    Invoice* m_invoice{};
    Invoice m_workingCopy;
    QTableView* m_table{};
    QStandardItemModel* m_model{};
    QPushButton* m_addButton{};
    QPushButton* m_editButton{};
    QPushButton* m_deleteButton{};
};

class PlanAnalysisDialog : public QDialog {
    Q_OBJECT
public:
    explicit PlanAnalysisDialog(QWidget* parent = nullptr);

    void setPlan(const PlanItem& plan);
    void refreshAnalysis();

private:
    PlanItem m_plan;
    QLabel* m_titleLabel{};
    QLabel* m_requiredLabel{};
    QLabel* m_availableLabel{};
    QLabel* m_statusLabel{};
    QLabel* m_timeframeLabel{};
    QLabel* m_breakdownLabel{};
    QTextEdit* m_adviceEdit{};
};
