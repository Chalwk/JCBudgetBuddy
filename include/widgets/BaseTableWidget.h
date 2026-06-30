// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QWidget>
#include <QTableView>
#include <QStandardItemModel>
#include <QPushButton>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QHeaderView>

class BaseTableWidget : public QWidget {
    Q_OBJECT
public:
    explicit BaseTableWidget(QWidget* parent = nullptr);

    virtual void refresh() = 0;

signals:
    void rowsMoved(int fromRow, int toRow);

protected:
    int selectedRow() const;
    void setupTable(const QStringList& headers);
    void setupButtons(const QString& addText = "Add", const QString& editText = "Edit", const QString& deleteText = "Delete");

    QTableView* table() const { return m_table; }
    QStandardItemModel* model() const { return m_model; }
    QPushButton* addButton() const { return m_addButton; }
    QPushButton* editButton() const { return m_editButton; }
    QPushButton* deleteButton() const { return m_deleteButton; }

private:
    QTableView* m_table{};
    QStandardItemModel* m_model{};
    QPushButton* m_addButton{};
    QPushButton* m_editButton{};
    QPushButton* m_deleteButton{};
};