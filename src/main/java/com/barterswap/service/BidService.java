package com.barterswap.service;

import com.barterswap.dto.bid.BidResponse;
import com.barterswap.dto.bid.PlaceBidRequest;
import com.barterswap.entity.Bid;
import com.barterswap.entity.Item;
import com.barterswap.entity.User;
import com.barterswap.entity.VirtualCurrency;
import com.barterswap.enums.ItemStatus;
import com.barterswap.exception.ItemException;
import com.barterswap.repository.BidRepository;
import com.barterswap.repository.ItemRepository;
import com.barterswap.repository.UserRepository;
import com.barterswap.repository.VirtualCurrencyRepository;
import com.barterswap.repository.TransactionRepository;
import com.barterswap.entity.Transaction;
import com.barterswap.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final VirtualCurrencyRepository virtualCurrencyRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BidResponse placeBid(PlaceBidRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ItemException("Item not found"));
        if (!item.getIsActive() || item.getIsDeleted() || item.getStatus() != ItemStatus.ACTIVE) {
            throw new ItemException("Cannot bid on inactive or deleted item");
        }
        if (item.getAuctionEndTime() != null && LocalDateTime.now().isAfter(item.getAuctionEndTime())) {
            throw new ItemException("Auction has ended");
        }
        if (item.getBuyoutPrice() != null && item.getCurrentPrice().compareTo(item.getBuyoutPrice()) >= 0) {
            throw new ItemException("Item already bought out");
        }
        if (request.getBidAmount().compareTo(item.getCurrentPrice()) <= 0) {
            throw new ItemException("Bid must be higher than current price");
        }
        // Check user balance
        VirtualCurrency userCurrency = virtualCurrencyRepository.findById(user.getVirtualCurrency().getVirtualCurrencyId())
                .orElseThrow(() -> new ItemException("User virtual currency not found"));
        if (userCurrency.getBalance().compareTo(request.getBidAmount()) < 0) {
            throw new ItemException("Insufficient virtual currency balance");
        }
        // Refund previous highest bidder
        Bid previousHighest = bidRepository.findByItemOrderByBidAmountDesc(item).stream().findFirst().orElse(null);
        if (previousHighest != null && previousHighest.getUser() != null && !previousHighest.getUser().getUserId().equals(user.getUserId())) {
            VirtualCurrency prevCurrency = virtualCurrencyRepository.findById(previousHighest.getUser().getVirtualCurrency().getVirtualCurrencyId())
                .orElseThrow(() -> new ItemException("Previous bidder's virtual currency not found"));
            prevCurrency.setBalance(prevCurrency.getBalance().add(previousHighest.getBidAmount()));
            virtualCurrencyRepository.save(prevCurrency);
        }
        // Deduct bid amount from current user
        userCurrency.setBalance(userCurrency.getBalance().subtract(request.getBidAmount()));
        virtualCurrencyRepository.save(userCurrency);
        // Save bid
        Bid bid = Bid.builder()
                .user(user)
                .item(item)
                .bidAmount(request.getBidAmount())
                .build();
        bid = bidRepository.save(bid);
        // Update item's current price
        item.setCurrentPrice(request.getBidAmount());
        // If buyout price is met or exceeded, end auction
        if (item.getBuyoutPrice() != null && request.getBidAmount().compareTo(item.getBuyoutPrice()) >= 0) {
            item.setStatus(ItemStatus.SOLD);
            item.setIsActive(false);
            // Create and save transaction
            Transaction transaction = Transaction.builder()
                .buyer(user)
                .seller(item.getUser())
                .item(item)
                .virtualCurrency(userCurrency)
                .price(request.getBidAmount())
                .status(TransactionStatus.COMPLETED)
                .build();
            transactionRepository.save(transaction);
        }
        itemRepository.save(item);
        return BidResponse.builder()
                .bidId(bid.getBidId())
                .itemId(item.getItemId())
                .userId(user.getUserId())
                .bidAmount(bid.getBidAmount())
                .timestamp(bid.getTimestamp())
                .build();
    }

    @Transactional(readOnly = true)
    public BidResponse getHighestBid(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException("Item not found"));
        Bid highest = bidRepository.findByItemOrderByBidAmountDesc(item).stream().findFirst()
                .orElseThrow(() -> new ItemException("No bids for this item"));
        return BidResponse.builder()
                .bidId(highest.getBidId())
                .itemId(item.getItemId())
                .userId(highest.getUser().getUserId())
                .bidAmount(highest.getBidAmount())
                .timestamp(highest.getTimestamp())
                .build();
    }
} 