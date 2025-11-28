//
//  Resource.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/16.
//

import Foundation
import Combine

struct Resource<T> {
    var data: T? = nil
    
    var message: String? = nil
    
    var state: State
    
    static func success(data: T) -> Resource {
        return Resource(data: data, state: .Success)
    }
    
    static func error(message: String, data: T? = nil) -> Resource {
        return Resource(data: data, message: message, state: .Error)
    }
    
    static func loading(data: T? = nil) -> Resource {
        return Resource(data: data, state: .Loading)
    }
    
    enum State {
        case Success
        case Error
        case Loading
    }
}

extension Publisher {
    func asResource() -> AnyPublisher<Resource<Output>, Never> {
        return self
            .map(Resource.success)
            .prepend(Resource.loading())
            .catch { error in
                Just(Resource.error(message: error.localizedDescription))
            }
            .eraseToAnyPublisher()
    }
}
