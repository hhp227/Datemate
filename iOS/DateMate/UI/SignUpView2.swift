//
//  SignUpView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/16.
//

import Foundation
import SwiftUI

struct SignUpView2: View {
    var onSignUp: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Button("Complete Register") {
                // viewModel.signUp()
                DependencyContainer.instance.set(true)
                onSignUp()
            }
        }
    }
}
