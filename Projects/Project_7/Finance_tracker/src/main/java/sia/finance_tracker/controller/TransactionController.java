//package sia.finance_tracker.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import sia.finance_tracker.entity.Transaction;
//import sia.finance_tracker.service.TransactionService;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/transactions")
//public class TransactionController {
//
//    private final TransactionService transactionService;
//
//    @Autowired
//    public TransactionController(TransactionService transactionService) {
//        this.transactionService = transactionService;
//    }
//
//    @PostMapping
//    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
//        Transaction createdTransaction = transactionService.addTransaction(transaction);
//        return ResponseEntity.ok(createdTransaction);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Transaction>> getTransactionsByUser(@PathVariable Long userId) {
//        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
//        return ResponseEntity.ok(transactions);
//    }
//}

package sia.finance_tracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.finance_tracker.entity.Transaction;
import sia.finance_tracker.entity.User;
import sia.finance_tracker.service.TransactionService;
import sia.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transactions", description = "transactions that a user makes.")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @Operation(
            summary = "Create a Transaction(NB: A user and Category must exist before creating a transaction).",
            description = "Create a Transaction object. The response is Transaction object with its id,name,user and category.",
            tags = { "Transaction", "post" })
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        transaction.setUser(user);
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @Operation(
            summary = "Update a Transaction.",
            description = "Update a Transaction object. The response is Transaction object with its id,name,user and category.",
            tags = { "Transaction", "put" })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        Transaction existingTransaction = transactionService.getTransactionById(id);
        if (existingTransaction == null || !existingTransaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized or transaction not found");
        }
        updatedTransaction.setUser(user);
        Transaction transaction = transactionService.updateTransaction(id, updatedTransaction);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Delete a Transaction.",
            description = "Delete a Transaction object. The response is Transaction object being deleted.",
            tags = { "Transaction", "delete" })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null || !transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized or transaction not found");
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a Transaction.",
            description = "Get a Transaction object. The response is Transaction object for a user.",
            tags = { "Transaction", "get" })
    @GetMapping
    public ResponseEntity<?> getTransactions(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        List<Transaction> transactions = transactionService.getTransactionsByUser(user.getId());
        return ResponseEntity.ok(transactions);
    }
}


