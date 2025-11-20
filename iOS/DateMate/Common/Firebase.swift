//
//  Firebase.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/17.
//

import Foundation
import FirebaseDatabase
import FirebaseAuth
import Combine

extension DatabaseReference {
    func observer(for event: DataEventType) -> Database.Publisher {
        return Database.Publisher(for: self, on: event)
    }
    
    func observeSingleEvent(of event: DataEventType) -> Future<DataSnapshot, Never> {
        Future { promise in
            self.observeSingleEvent(of: event) { snapshot in
                promise(.success(snapshot))
            }
        }
    }
}

extension Database {
    struct Publisher: Combine.Publisher {
        typealias Output = DataSnapshot
        
        typealias Failure = Never
        
        private var reference: DatabaseReference
        
        private var event: DataEventType
        
        init(for reference: DatabaseReference, on event: DataEventType) {
            self.reference = reference
            self.event = event
        }
        
        func receive<S>(subscriber: S) where S: Subscriber, Publisher.Failure == S.Failure, Publisher.Output == S.Input {
            let subscription = Subscription(subscriber: subscriber, reference: reference, event: event)
            
            subscriber.receive(subscription: subscription)
        }
    }
    
    final class Subscription<SubscriberType: Subscriber>: Combine.Subscription where SubscriberType.Input == DataSnapshot {
        private var reference: DatabaseReference?
        
        private var handle: UInt?
        
        init(subscriber: SubscriberType, reference: DatabaseReference, event: DataEventType) {
            self.reference = reference
            handle = reference.observe(event) { snapshot in
                _ = subscriber.receive(snapshot)
            }
        }
        
        func request(_ demand: Subscribers.Demand) {
            
        }
        
        func cancel() {
            if let handle = handle {
                reference?.removeObserver(withHandle: handle)
            }
            handle = nil
            reference = nil
        }
    }
}

// DatabaseReference+Combine
public typealias DatabaseReferenceTransactionResult = (committed: Bool, snapshot: DataSnapshot?)

extension DatabaseReference {
    public func setValue(_ value: Any?, andPriority priority: Any? = nil) -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.setValue(value, andPriority: priority, withCompletionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func removeValue() -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.removeValue(completionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func setPriority(_ priority: Any?) -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.setPriority(priority, withCompletionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func updateChildValues(_ values: [AnyHashable: Any]) -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.updateChildValues(values, withCompletionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func onDisconnectSetValue(_ value: Any?, andPriority priority: Any? = nil) -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.onDisconnectSetValue(value, andPriority: priority, withCompletionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func onDisconnectRemoveValue() -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.onDisconnectRemoveValue(completionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func onDisconnectUpdateChildValues(_ values: [AnyHashable: Any]) -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.onDisconnectUpdateChildValues(values, withCompletionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func cancelDisconnectOperations() -> Future<DatabaseReference, Error> {
        Future<DatabaseReference, Error> { [weak self] promise in
            self?.cancelDisconnectOperations(completionBlock: { (error, ref) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(ref))
                }
            })
        }
    }
    
    public func runTransactionBlock(_ block: @escaping (MutableData) -> TransactionResult, withLocalEvents: Bool = true) -> Future<DatabaseReferenceTransactionResult, Error> {
        Future<DatabaseReferenceTransactionResult, Error> { [weak self] promise in
            self?.runTransactionBlock(block, andCompletionBlock: { (error, committed, snapshot) in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(DatabaseReferenceTransactionResult(committed, snapshot)))
                }
            })
        }
    }
}
