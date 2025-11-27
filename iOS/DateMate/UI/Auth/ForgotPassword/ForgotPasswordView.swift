//
//  ForgotPasswordView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI

struct ForgotPasswordView: View {
    @StateObject var viewModel: ForgotPasswordViewModel = DependencyContainer.instance.provideForgotPasswordViewModel()
    
    var onBackToSignIn: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .center) {
                Spacer().frame(height: 64)
                Text("Forgot Your Password?")
                    .font(.title)
                    .fontWeight(.bold)
                    .padding(.bottom, 16)
                Text("Enter your email address to receive a password reset link.")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 32)
                EmailField(
                    value: viewModel.uiState.email,
                    error: viewModel.uiState.emailError,
                    onValueChange: viewModel.onEmailChange,
                    onSubmit: viewModel.sendResetEmail
                )
                Spacer().frame(height: 24)
                Button(action: viewModel.sendResetEmail) {
                    if viewModel.uiState.isLoading {
                        ProgressView()
                            .frame(width: 24, height: 24)
                    } else {
                        Text("Send Reset Link")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity, minHeight: 52)
                .background(viewModel.uiState.isLoading ? Color.gray.opacity(0.3) : Color.blue)
                .foregroundColor(.white)
                .cornerRadius(16)
                .disabled(viewModel.uiState.isLoading)
                if let message = viewModel.uiState.message {
                    Spacer().frame(height: 16)
                    Text(message)
                        .foregroundColor(
                            viewModel.uiState.isEmailSent ?
                            Color.green : Color.red
                        )
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 8)
                        .frame(maxWidth: .infinity)
                }
                Spacer(minLength: 40)
                Button(action: onBackToSignIn) {
                    Text("Back to Sign In")
                }
                .padding(.top, 16)
            }
            .padding(16)
        }
        .onChange(of: viewModel.uiState.isEmailSent) { value in
            if value {
                Task {
                    try? await Task.sleep(nanoseconds: 3_000_000_000)
                    viewModel.consumeMessage()
                    onBackToSignIn()
                }
            }
        }
    }
}

struct ForgotPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ForgotPasswordView(onBackToSignIn: {})
    }
}
