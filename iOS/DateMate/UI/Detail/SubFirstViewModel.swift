//
//  SubFirstViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/13.
//

import Foundation
import SwiftUI
import Combine

class SubFirstViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    @Published var data: String
    
    private var cancellables = Set<AnyCancellable>()
    
    func signOut() {
        userRepository.getSignOutResultStream()
            .receive(on: DispatchQueue.main)
            .sink { result in
                /*switch result {
                case .loading: break
                case .success: break
                case .error: break
                default:
                    <#code#>
                }*/
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository, data: String) {
        self.userRepository = userRepository
        self.data = data
    }
}
