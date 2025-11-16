//
//  SignInView2.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/16.
//

import SwiftUI

struct SignInView2: View {
    @StateObject private var viewModel = SignInViewModel2(DependencyContainer.instance.userRepository)
    
    var onSignUp: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Button("Login") {
                // viewModel.login()
                DependencyContainer.instance.set(true)
            }
            Button("Register") {
                onSignUp()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .multilineTextAlignment(.center)
    }
}

struct SignInView2_Previews: PreviewProvider {
    static var previews: some View {
        SignInView2(onSignUp: {})
    }
}
