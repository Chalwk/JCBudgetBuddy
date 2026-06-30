#include "delegates/ButtonDelegate.h"

#include <QApplication>
#include <QMouseEvent>
#include <QPainter>
#include <QStyleOptionButton>

ButtonDelegate::ButtonDelegate(const QString& text, QObject* parent)
    : QStyledItemDelegate(parent), m_text(text) {}

void ButtonDelegate::paint(QPainter* painter, const QStyleOptionViewItem& option, const QModelIndex&) const {
    QStyleOptionButton button;
    button.rect = option.rect.adjusted(6, 4, -6, -4);
    button.text = m_text;
    button.state = QStyle::State_Enabled;
    if (option.state & QStyle::State_MouseOver) {
        button.state |= QStyle::State_MouseOver;
    }
    QApplication::style()->drawControl(QStyle::CE_PushButton, &button, painter);
}

bool ButtonDelegate::editorEvent(QEvent* event, QAbstractItemModel*, const QStyleOptionViewItem& option, const QModelIndex& index) {
    if (event->type() == QEvent::MouseButtonRelease) {
        auto* mouse = static_cast<QMouseEvent*>(event);
#if QT_VERSION >= QT_VERSION_CHECK(6, 0, 0)
        const QPoint point = mouse->position().toPoint();
#else
        const QPoint point = mouse->pos();
#endif
        if (option.rect.contains(point)) {
            emit clicked(index);
            return true;
        }
    }
    return false;
}
