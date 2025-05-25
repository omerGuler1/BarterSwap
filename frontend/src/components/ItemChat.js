import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getItemMessages, sendMessage } from '../api';
import '../styles/ItemChat.css';

const ItemChat = ({ itemId, sellerId }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const [selectedBuyerId, setSelectedBuyerId] = useState(null);
    const { user } = useAuth();

    console.log("ItemChat rendered, isOpen:", isOpen); // Debug log

    useEffect(() => {
        if (isOpen) {
            loadMessages();
        }
    }, [isOpen, itemId]);

    const loadMessages = async () => {
        try {
            const response = await getItemMessages(itemId);
            setMessages(response.data);
        } catch (error) {
            console.error('Error loading messages:', error);
        }
    };

    // Extract unique buyers from messages (for seller view)
    const buyers = Array.from(
        new Set(
            messages
                .filter(msg => msg.senderId !== sellerId)
                .map(msg => msg.senderId)
        )
    );

    // Get buyer name by id
    const getBuyerName = (buyerId) => {
        const msg = messages.find(
            m => m.senderId === buyerId || m.receiverId === buyerId
        );
        return msg ? (msg.senderId === buyerId ? msg.senderName : msg.receiverName) : `User #${buyerId}`;
    };

    // Filter messages for seller-buyer conversation
    const filteredMessages = user.userId === sellerId && selectedBuyerId
        ? messages.filter(msg =>
            (msg.senderId === selectedBuyerId && msg.receiverId === sellerId) ||
            (msg.senderId === sellerId && msg.receiverId === selectedBuyerId)
        )
        : user.userId === sellerId
            ? []
            : messages.filter(msg =>
                msg.senderId === user.userId || msg.receiverId === user.userId
            );

    const handleSendMessage = async (e) => {
        console.log("handleSendMessage called");
        e.preventDefault();
        if (!newMessage.trim()) return;

        try {
            let receiverId;
            if (user.userId === sellerId) {
                // Seller must select a buyer
                if (!selectedBuyerId) {
                    alert('Select a buyer to chat with.');
                    return;
                }
                receiverId = selectedBuyerId;
            } else {
                receiverId = sellerId;
            }
            const messageData = {
                receiverId,
                itemId: itemId,
                content: newMessage
            };
            console.log("Sending message:", messageData);
            await sendMessage(messageData);
            setNewMessage('');
            loadMessages(); // Reload messages after sending
        } catch (error) {
            console.error('Error sending message:', error);
        }
    };

    if (!isOpen) {
        return (
            <div className="item-actions">
                <button 
                    className="btn btn-primary"
                    onClick={() => setIsOpen(true)}
                >
                    {user.userId === sellerId ? 'View Messages' : 'Question & Answer'}
                </button>
            </div>
        );
    }

    return (
        <div className="chat-container card">
            <div className="chat-header">
                <h3>{user.userId === sellerId ? 'Messages' : 'Question & Answer'}</h3>
                <button className="btn btn-secondary" onClick={() => setIsOpen(false)}>×</button>
            </div>
            
            {user.userId === sellerId && (
                <div className="buyer-list card mb-3">
                    <h4 className="mb-2">Buyers</h4>
                    {buyers.length === 0 ? (
                        <p className="text-light">No messages yet.</p>
                    ) : (
                        <div className="grid grid-2 gap-2">
                            {buyers.map(buyerId => (
                                <button
                                    key={buyerId}
                                    className={`btn ${selectedBuyerId === buyerId ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => setSelectedBuyerId(buyerId)}
                                >
                                    {getBuyerName(buyerId)}
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            )}

            {user.userId === sellerId && !selectedBuyerId && buyers.length > 0 && (
                <div className="alert alert-info">
                    Select a buyer to view the conversation.
                </div>
            )}

            <div className="messages-container">
                {filteredMessages.length === 0 ? (
                    <div className="text-center text-light p-4">
                        {user.userId === sellerId 
                            ? 'Select a buyer to view messages'
                            : 'No messages yet. Start the conversation!'}
                    </div>
                ) : (
                    filteredMessages.map((message) => (
                        <div 
                            key={message.messageId} 
                            className={`message ${message.senderId === user.userId ? 'sent' : 'received'}`}
                        >
                            <div className="message-header">
                                <span className="sender-name">
                                    {message.senderId === user.userId ? 'You' : message.senderName}
                                </span>
                                <span className="message-time">
                                    {new Date(message.sentAt).toLocaleString()}
                                </span>
                            </div>
                            <div className="message-content">{message.content}</div>
                        </div>
                    ))
                )}
            </div>

            <form onSubmit={handleSendMessage} className="message-input-form">
                <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Type your message..."
                    className="form-control"
                    disabled={user.userId === sellerId && !selectedBuyerId}
                />
                <button 
                    type="submit" 
                    className="btn btn-primary"
                    disabled={user.userId === sellerId && !selectedBuyerId}
                >
                    Send
                </button>
            </form>
        </div>
    );
};

export default ItemChat; 